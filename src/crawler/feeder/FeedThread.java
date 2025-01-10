package crawler.feeder;

import java.util.List;

import crawler.client.CrawlerClient;
import crawler.config.Config;
import crawler.filter.IFilter;
import crawler.filter.URLFilter;
import crawler.io.FileURLProvider;
import crawler.io.IURLProvider;
import crawler.mysql.SQLMovie;
import crawler.url.ShortenURL;
import crawler.url.URLUtil;
//import idc.crawler.entity.URLEntity;

public class FeedThread implements Runnable {
	
	public static boolean CHECKHOST = Config.getInstance().isCheckhost(); //是否指定爬行范围
	
	IFilter filter = new URLFilter();
	List<String> allowDomain = null;
	private SQLMovie sqlMovie = null;
	public IURLProvider urlProvider = new FileURLProvider(); // 获取是否缓存过链接
	public static boolean useDatabase = Config.getInstance().isUseDatabase();
	
	public FeedThread() {
		//是否限制只爬行某些域名主机
		allowDomain = Config.getInstance().getAllowDomain();
		if(CHECKHOST && (allowDomain == null || allowDomain.isEmpty())) {
			System.err.println("指定爬行主机,但没有指定域名范围,程序将强行退出!");
			System.exit(-1);
		}
		if (useDatabase) 
			sqlMovie = new SQLMovie();
	}
	
	@Override
	public void run() { //启动主线程进行URL判重操作
//		URLEntity urlEntity;
		String tempUrl = null;
		String hostdomain = null;
		//int count = 0;
		boolean flag = false;
		int tempHashCode = -1;
		String checkInHash = null;
		while(CrawlerClient.running) { //进行URL检测(是否属于自己任务)及URL判重操作
			tempUrl = Task.urlsToTest.poll();
		   // System.out.println("url"+tempUrl);
			
			if(tempUrl == null) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {	}
				continue;
			}

			//url = URLUtil.toUTF8(url);
			hostdomain = URLUtil.getDomain(tempUrl);
			// filter url
			
	
			
			
			if(filter.isFilterUrl(tempUrl, hostdomain)){
				//System.out.println("[Feedthread.java57 过滤]:" + tempUrl);
				continue;
			} //else 
				//System.out.println("[Feedthread.java57 过滤]:" + tempUrl);
				
           
			// shorten url
			for(int i=0; i<ShortenURL.ShortenDomain.length; i++){
				if(hostdomain.equals(ShortenURL.ShortenDomain[i])){
					tempUrl = ShortenURL.DefaultShorten(tempUrl, i);
					break;
				}
			}
				
			if(useDatabase && sqlMovie != null){ //如果数据库中有次链接，则不再进行爬行，这里有一个问题
				try{
				if(sqlMovie.urlFilter(tempUrl))
					continue;
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			//判断是否处理过该URL,如果没有,则添加处理标志,同时返回false
			checkInHash = CrawlerClient.bfilter.checkAndAdd(tempUrl);
			if(checkInHash != null) {
				tempHashCode = URLUtil.getHashcode(tempUrl);
				if(CrawlerClient.hashCode.contains(tempHashCode)) {
					/*urlEntity = new URLEntity();
					urlEntity.setHashcode(tempHashCode);
					urlEntity.setCheckInHash(checkInHash);
					urlEntity.setType(type);
					urlEntity.setUrl(tempUrl);
					urlEntity.setUrlHost(urlHost);*/
					if(!flag && Task.urlsToCrawl.size() > 2 * IURLProvider.savePerSize){
						flag = true;
					}
					if(flag){
						Task.urlsToCache.add(tempUrl);  // 保证宽度优先搜索
						//System.out.println("add url into Task.urlsToCache");

					} else {
						Task.urlsToCrawl.add(tempUrl);
					//	System.out.println(url);
						//count++;
						//System.out.println("FeedThread. add url to Task.urlsToCrawl." + Task.urlsToCrawl.size() + "." + count);
					}
				} else {
					Task.urlsToDispatch.get(tempHashCode).add(tempUrl);
				}
			}
		} // end while
	} // end run
	
	//测试
	public static void main(String args[]) {
	    String url="http://movie.douban.com/subject/6082518/?from=showing";

	   
	    FeedThread fh=new FeedThread();
	    new Thread(fh);
		System.out.println(url);
	}
}
