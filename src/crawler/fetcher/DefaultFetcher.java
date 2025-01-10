package crawler.fetcher;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;



import crawler.client.CrawlerClient;
import crawler.config.Config;
import crawler.crawler.Crawler;
import crawler.entity.Image;
import crawler.entity.Movie;
import crawler.entity.Page;
import crawler.feeder.Task;
import crawler.filter.URLFilter;
import crawler.mysql.SQLMovie;
import crawler.util.FeatureUtils;
import crawler.util.HttpUtil;
import crawler.url.URLUtil;
import crawler.parse.DefaultParse;

/**
 * 该下类做为默认下载类,并不真正执行下载任务.本类的作用是获取
 * 待爬URL,打开连接,获取其信息头ContentType,然后根据该信息头
 * 判断URL对应的文件类型(视频,音频,图像或普通文本,或者是其他
 * 爬虫不处理的类型),然后根据类型选择合适的下载类进行下载.
 * 这样做便于日后扩展.
 */
public class DefaultFetcher{
	
	public static Config config = Config.getInstance();
	private HashSet<String> allowImageType = config.getAllowImageType();
	public static boolean useDatabase = config.isUseDatabase();
	public final static int MAX_IMAGE_SIZE = config.getMaxImageSize();
	public final static int MIN_IMAGE_SIZE = config.getMinImageSize();
	public static boolean savedLocal = config.isSavedLocal();
	public static boolean savedHbase = config.isSavedHbase();
	
	private FeatureUtils ftu = new FeatureUtils();   //发送图片特征
	private BufferedInputStream input = null;
	private ByteArrayOutputStream output = null;
	private byte[] readbuf;
	
	private SQLMovie sqlMovie = null;
	private Timestamp timestamp = null;
	
	private FetcherToLocalFS imageFetcher = null;
	private FetcherToHbase fetcherInHbase = null;

	private boolean updateFlag = false;   //用于标记是否是更新线程调用本类
	
	public boolean isUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(boolean updateFlag) {
		this.updateFlag = updateFlag;
	}

	public DefaultFetcher(){
		readbuf = new byte[2048];
		if (useDatabase) 
		{
			sqlMovie = new SQLMovie();
		}
		if(savedLocal)
			imageFetcher = new FetcherToLocalFS();
		if(savedHbase)
			fetcherInHbase = new FetcherToHbase();
	}
	
	public boolean fetch(String url) throws Exception {
		java.net.Authenticator.setDefault(new crawler.util.AuthenticatorEntity());
		HttpURLConnection conn = null;
		conn = HttpUtil.getConnection(url);
		// 时刻注意:这个连接对象要由下载者自己关闭.如果没有对象处理它,则须自己关闭

//		String name = Thread.currentThread().getName();
		boolean done = false;
		//List<String> tempt = null
		int responseCode = conn.getResponseCode();
		
		
		if (responseCode >= 300 && responseCode < 400) {
			String location = conn.getHeaderField("location");
			if (location == null)
				location = conn.getHeaderField("Location");
			if (location != null)
				Task.urlsToTest.add(location);
			conn.disconnect();
			return true;
		}
		if (responseCode != HttpURLConnection.HTTP_OK) {
			conn.disconnect();
			return false;
		}
		String contentType = conn.getContentType();
//		System.err.println(url + ", contentType:" + contentType);
		int type = checkContentType(contentType);
		if (type == -1) // 无法从contentType检测,尝试从URL格式检测
			type = URLUtil.getUrlType(url);
		
		if (type == Page.TEXT) {
				//Bag bag = new Bag();
			    Movie movie=new Movie();
				HashSet<String> set = DefaultParse.Parse(url, movie);	
			//	System.out.println("set size"+set.size());
				if(set != null)
				  Task.urlsToTest.addAll(set);			

				
//				for(Iterator it=Task.urlsToTest.iterator();it.hasNext();)
//				{
//					System.out.println(it.next().toString());
//				}
				
				// 下载图片

					if(movie.getImgSet()!=null && movie!=null){
						if(downloadImage(movie)){
							//System.out.println("Download Image success.");
						} /*else{
							System.out.println("Download Image fail.");
						}*/
					}

				conn.disconnect();
				done = true;
				//System.out.println("线程" + Thread.currentThread().getName() + 
				//"分析页面:" + url + ",提取链接" + tempt.size() + "个");

		} else {
			conn.disconnect(); // 不处理的类型.暂时直接返回true
			done = true;
		}
		
		return done;
	}
	


	
	public boolean downloadImage(Movie movie) throws Exception{
		boolean flag = true;
		int count=0;
		HashSet<Image> imgSet = movie.getImgSet();
		HashSet<Image> set1 = new HashSet<Image>();  //用于存储movie对象中不符合要求的图片对象
		HashSet<String> set2 = new HashSet<String>();  //用于存储movie对象中不符合要求的图片的链接
		Image image;
		String url;
	    for(Iterator it=imgSet.iterator();it.hasNext();)
	    {
	    	image=(Image) it.next();
			url = image.getImageURL();
			HttpURLConnection conn = HttpUtil.getConnection(url);
			
			
			//获取图像格式
			String format = URLUtil.getUrlSuffix(url); //url.substring(url.lastIndexOf('.') + 1);
			if(format == null) {
				int pos = url.lastIndexOf('?');
				if(pos == -1)	pos = url.length();
				format = url.substring(url.lastIndexOf('.', pos) + 1, pos).toLowerCase();
			}
			//判断该格式是否允许下载
			if(!allowImageType.contains(format)) {
				set1.add(image);
				set2.add(image.getImageURL());
				conn.disconnect();
				continue;
			}
			image.setImageType(format);
			
	        //开始下载
	        int totalsize = 0;
	        try {
	        	int size = conn.getContentLength();
	        	if(size > 0 && size < MIN_IMAGE_SIZE)
	        	{
	    			set1.add(image);
					set2.add(image.getImageURL());
	        		continue;
	        	}
	            input = new BufferedInputStream(conn.getInputStream());
	            //output = new FileOutputStream(file);
	            output = new ByteArrayOutputStream();
	            while((size = input.read(readbuf)) != -1) {
	            	output.write(readbuf, 0, size);
	            	totalsize += size;
	            }
	            output.flush();
	            
	            // 图片存在本地文件系统
				if(savedLocal){
					String physicsPath = imageFetcher.fetch(format, output.toByteArray());
					if(physicsPath != null){
						ftu.sendFeature(physicsPath);
						String imgName = physicsPath.substring(physicsPath.lastIndexOf('\\') + 1);			
						//physicsPath  = Crawler.rootAddress + physicsPath;
						/*
						String rootAddress = Crawler.rootAddress;
			    		if(rootAddress.endsWith(File.separator))
			    			rootAddress = rootAddress.substring(0, rootAddress.length() - 1);
	
			    		String rootPath = Crawler.rootPath;
			    		if(rootPath.endsWith(File.separator))
			    			rootPath = rootPath.substring(0, rootPath.length() - 1);
			    	
			    		physicsPath  = physicsPath.replace(rootPath, rootAddress);
						*/
						//System.out.println("PhysicsPath: " + physicsPath);
						//System.out.println("imgName: " + imgName);

						image.setImageName(imgName);
						image.setPhysicsPath(physicsPath);
						image.setImageType(format);
					    Task.crawledImages.incrementAndGet(); //记录成功抓取图像个数	
			    		count++;   
					} else{         //如果图片不符合要求，则从海报图片集合中删除	
						set1.add(image);
						set2.add(image.getImageURL());
					}
						
				}
				
				// 图片存储在Hbase
				if(savedHbase){
					
					// Oracle数据库中序列实现文件名递增 
					String imgName = new StringBuilder(20).append(sqlMovie.getSeqImgName()).toString();
					fetcherInHbase.fetch(imgName, output.toByteArray());
					image.setImageName(imgName);
					image.setPhysicsPath("null");
					//System.out.println("upload image to Hbase.");
				}
	           
	        } finally {
	        	if(input != null)        		input.close();
	        	if(output != null)        		output.close();
	        	if(conn != null)        		conn.disconnect();
	        	
	        	if(totalsize <= 0) {
	        		continue;
	        	}
	        	if (totalsize <= MIN_IMAGE_SIZE
						|| (MAX_IMAGE_SIZE != -1 && totalsize > MAX_IMAGE_SIZE)) {
	        		if(CrawlerClient.debug)
	            		Task.logger.info("文件 " + url + " 大小不合要求: + " + totalsize + "丢弃");
	        		System.out.println("DefaultFetcher. 文件大小不合要求丢弃. " + totalsize);
	        		continue; //文件丢弃了.但仍返回true,表示该地址被成功抓取
	        	}	
	        }
	    } 
	    
	    for(Iterator it=imgSet.iterator();it.hasNext();)  //某些时候由于网速不好导致图片下载失败，这些图片没有名称，因此需要剔除。
	    {
	    	image=(Image) it.next();
	    	if(image.getImageName()==null)
	    	{
	    		set1.add(image);
	    		set2.add(image.getImageURL());
	    	}
	    }
	    
	    if(set1.size()!=0) //移除没有名称的图片链接
	    {
	    	imgSet.removeAll(set1);
			movie.getImgUrlSet().removeAll(set2);
	    }

	   	
	    if(count!=0)  //如果此电影的海报图片数量不为0，则将此电影对象存入数据库中
	    	flag=true;
	    else
	        flag=false;
//	 
        if (useDatabase &&  flag && sqlMovie != null) {
        	//保存图像文件信息到数据库
           // System.out.println("执行数据库");
        	timestamp = new Timestamp(System.currentTimeMillis());
        	movie.setCrawlerTime(timestamp);
        	movie.setModifyTime(timestamp);
        	
	        if(!updateFlag){
	        	// 插入到数据库
	        	int total = sqlMovie.insertMovie(movie);
	        	if(total != 1) {
	        		if(CrawlerClient.debug)
	        			System.out.println("图片保存到数据库失败!" + movie.getUrl());
	        		return false;
	        	}
	        }
        }
	    
	  //  System.out.println(movie.toString());
       return flag;
	}
	
	
	public int checkContentType(String contentType) {
		int type = -1; // 做为默认类型返回
		if (contentType == null || contentType.isEmpty())
			return type;
		contentType = contentType.split("[; ]")[0].toLowerCase();
		if (contentType.indexOf("text") != -1)
			type = Page.TEXT;
		else if (Image.MimeTypeImage.containsKey(contentType))
			type = Page.IMAGE;
		return type;
	}
	

	

	/**
	 * 根据类型获取相应的下载器(图像,音频,视频)
	 * 
	 * @param type
	 * @return
	 */
	// 这种情形非常典型,应该用工厂模式来实现.可我真的有很多问题,比如说图像和音频用一个类实现,
	// 还是用不同的类实现?它们的处理方法几乎一样!还有视频,我应该现在下载它,还是将它丢进视频队
	// 列,让线程池去下载呢?还是我为了模式的完整性,实现这样一个类,但是这个类并不真正实现下载,
	// 而只是简单的将这个地址丢进视频下载地址队列中??我还要考虑,看后面的情况再做决定.苦恼啊!
	/*private IFetcher getDownloader(int type) {
		if (type == Media.IMAGE)
			return imageFetcher;
		else if (type == Media.AUDIO)
			return audioFetcher;

		return null;
	}*/

	public static void main(String[] args) throws Exception {
		/*String surl = "http://tjb.web.xtu.edu.cn:8082/lanqiu/yanhu.mpg";
		java.net.URL url = new java.net.URL(surl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		String contentType = conn.getContentType();
		System.out.println("contentType:" + contentType);
		
		
		surl = "http://www.tuku.cn/look.aspx?url=pic/wallpaper/qita/lipinbaozhuang/018.jpg";*/
		//IFetcher fetcher = new DefaultFetcher();
		//fetcher.fetch(surl);
		//String physicsPath = "/home/hdusr/CrawlerDownload/image/jpg/jpg1/24-1394765284873.jpg";
//		String imgName = physicsPath.substring(physicsPath.lastIndexOf('/') + 1);
		DefaultFetcher fetcher=new DefaultFetcher();
		String url1="http://www.m1905.com/mdb/film/2210468/";
		String url2="http://www.m1905.com/mdb/film/2218153/";
		String url3="http://www.m1905.com/mdb/film/55284";
		String url4="http://www.m1905.com/mdb/film/55284/#comment";
		String url5="http://www.m1905.com/mdb/film/1970408/";
		String url6="http://image11.m1905.cn/uploadfile/2014/0430/20140430091646807115.jpg";
//		Movie movie=new Movie();
//		DefaultParse.Parse(url, movie);
//		
//		fetcher.downloadImage(movie);
     //   fetcher.fetch(url1);
       // fetcher.fetch(url2);
	//	URLFilter filter1=new URLFilter();
//		System.out.println(physicsPath);	
		//fetcher.fetch(url5);
		System.out.println(Crawler.rootAddress);
	}

}
