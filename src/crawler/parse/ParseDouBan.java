package crawler.parse;

import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.net.URL;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;

import crawler.entity.Movie;
import crawler.entity.Page;
import crawler.fetcher.DefaultFetcher;
import crawler.url.URLUtil;

public  class ParseDouBan {
	
	
	/**
	* 功能：解析豆瓣网电影信息,提取电影的导演、编剧、主演、类型以及电影的海报链接和所要分析网页的所有链接等
	* 原理：采用Filter访问html,Filter是根据某种条件过滤取出需要的Node再进行处理
	* @author XiaoRui 
	*/
	public static HashSet<String> filter(String url, Movie movie){
		HashSet<String> linkSet = new HashSet<String>();    
		HashSet<String> imgUrlSet=new HashSet<String>();
		HashSet<String> videoUrlSet=new HashSet<String>();
//		boolean isMovieItem = false;           //判断网页是否是电影或者电视剧简介
//		int urlWeight = url.charAt(0) - '0';        //网页权值
//		int tempWeight;
//		url = url.substring(1);
		
//		if(url.startsWith("http://movie.douban.com/subject/"))
//			isMovieItem = true;
		try{
			Parser parser=new Parser();
			//parser.setURL(url);
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);

			
			// 链接/Meta标签
			NodeFilter linkNF = new NodeClassFilter(LinkTag.class);
			NodeFilter metaNF = new NodeClassFilter(MetaTag.class);
			NodeFilter titleNF = new NodeClassFilter(TitleTag.class);
		
			//电影的详细信息以及电影简介
			NodeFilter divNF = new NodeClassFilter(Div.class);
			NodeFilter divAttNF = new HasAttributeFilter("id","info");
			NodeFilter divAF = new AndFilter(new NodeFilter[]{divNF, divAttNF});
			
            NodeFilter spanNF=new NodeClassFilter(Span.class);
			NodeFilter spanAttNF = new HasAttributeFilter("property","v:summary");
			NodeFilter spanAF = new AndFilter(new NodeFilter[]{spanNF,spanAttNF});	
			
			//电影的评分
			NodeFilter scoreNF=new HasAttributeFilter("id","interest_sectl");
			/*NodeList list1=parser.extractAllNodesThatMatch(scoreNF);
			System.out.println(list1.size());*/
			
			//电影的海报图片地址
			NodeFilter imgNF=new HasAttributeFilter("id","mainpic");
			
			//电影的预告片地址
			NodeFilter videoNF = new HasAttributeFilter("id","related-pic");
			
			
			OrFilter filter=new OrFilter(new NodeFilter[]{linkNF, metaNF, divAF,spanAF, scoreNF,imgNF,titleNF,videoNF});
			NodeList list=parser.extractAllNodesThatMatch(filter);

			boolean flag = false;
			for(int i=0;i<list.size();i++){
				Tag tag = (Tag)list.elementAt(i);
				if (tag instanceof LinkTag) {
					LinkTag linkTag = (LinkTag)tag;
		        	if(linkTag.isHTTPLikeLink()){
		        		String str = linkTag.extractLink(); 
//		             if(urlWeight != 9)
//		             {	if(isMovieItem)    
//		        		{
//		        		   if(flag)
//		        		   {
//		        			   if(str.startsWith("http://movie.douban.com/subject/"))
//		        				   tempWeight = urlWeight; 
//		        			   else
//		        				   tempWeight = urlWeight - 1;  			   
//		        		   }else
//		        			   tempWeight = urlWeight - 2; 
//		        		}else{
//		        			if(str.startsWith("http://movie.douban.com/subject/"))
//		        				tempWeight = urlWeight + 1;
//		        			else
//		        				tempWeight = urlWeight;
//		        			
//		        		}
//		            	if(tempWeight < 0)
//		       			   tempWeight = 0;
//		        	    if(tempWeight > 8)
//		        	    	tempWeight = 8;
//		        	}else{
//		        		if(str.startsWith("http://movie.douban.com/subject/"))
//	        			   tempWeight = urlWeight - 1; 
//		        		else
//	        			   tempWeight = urlWeight - 2;  
//		        	}
//		      
//		        		str = (char)(tempWeight + '0') +str; 
		        		//System.out.println(str);
		        		linkSet.add(str);
		        	}
				}else if(tag instanceof MetaTag){
					String temp = tag.getAttribute("name");
					if (temp != null && "description".equalsIgnoreCase(temp)) {
						String content = tag.getAttribute("content");
						if(content != null && content.contains("电影简介")){		
							movie.setUrl(url);
							movie.setDomain(URLUtil.getDomain(url));
							flag = true;
						}
					}
					
				}else if(tag instanceof TitleTag){
					String title = tag.getFirstChild().getText();
					int temp = title.indexOf("(豆瓣)");
					if(temp!=-1)
					{
						title = title.substring(0, title.indexOf("(豆瓣)")).trim();
						movie.setTitle(title);
					}	
				}else if(flag && tag instanceof Div) {      //获取电影的详细信息及评分
					
					 if("info".equalsIgnoreCase(tag.getAttribute("id")))   //获取电影的详细信息
					 {
						 String str=tag.toPlainTextString();
						 str=str.replaceAll(" ", "").trim();
						 movie.setMovieInfo(str);
						 //System.out.println("电影信息"+'\n'+str);
					 }
					 else if("interest_sectl".equalsIgnoreCase(tag.getAttribute("id")))  //获取电影的评分
					 {
						 String str=tag.toPlainTextString();
						 str=str.replaceAll(" ", "").replaceAll("\\n", "").trim();
						 movie.setScore(ScoreInfo(str));
						 //System.out.println("电影评分"+'\n'+ScoreInfo(str));
					 }	
					 else if("mainpic".equalsIgnoreCase(tag.getAttribute("id")))     //获取电影的海报图片链接地址
					 {
						 String str=tag.toHtml();
						 int temp=str.indexOf("href")+6;
						 str=str.substring(temp, str.indexOf("title",temp)-2);
						 imgUrlSet=imgUrlFilter(str);
						 movie.setImgUrlSet(imgUrlSet);
//						for(Iterator it=imgUrlSet.iterator();it.hasNext();)
//						{
//							System.out.println(it.next());
//						}
						// System.out.println(str);
					 }
					 else if("related-pic".equalsIgnoreCase(tag.getAttribute("id")))         //获取电影的预告片链接
					 {
						 String str = tag.toHtml();
						 int temp=str.indexOf("href")+6;
						 str = str.substring(temp, str.indexOf(">", temp)-1);
						 videoUrlSet = videoUrlFilter(str);
						 movie.setVideoUrlSet(videoUrlSet);
					 }
		    	}else if(flag && tag instanceof Span){         //获取电影简介
		    		if("v:summary".equalsIgnoreCase(tag.getAttribute("property"))){
		    			String str = tag.toPlainTextString().replaceAll("\r|\n", "").trim();
		    			movie.setMovieAbstract1(str);
		    		}
		    	}
		    		
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return linkSet;
	}
	
	/**
	  *功能：分解字符串，获取详细的评分信息
	  *@author XiaoRui 
	 **/
	public static String ScoreInfo(String str)
	{
		String score="";
		//System.out.println("url"+url);
		if(str.contains("尚未上映") || str.contains("评价人数不足"))
			return score;
		int temp=str.indexOf("(");
		if(temp!=-1)
		score=str.substring(0, temp)+'分';
        return score;
	}
	
	/**
	 * 功能：获取海报图片链接集合
	 * @author XiaoRui
	 */
	public static HashSet<String> imgUrlFilter(String url)
	{
		HashSet<String> set = new HashSet<String>();
		try{

			Parser parser=new Parser();
			//parser.setURL(url);
			parser.setEncoding(parser.getEncoding());
		
			//模拟IE浏览器访问豆瓣电影网站，以防止被网站屏蔽
		    URL urls = new URL(url);
		    HttpURLConnection connection =  (HttpURLConnection)urls.openConnection();
		    connection.setRequestProperty("User-agent","IE/6.0"); 
		    parser.setConnection(connection);
			
			NodeFilter divNF = new NodeClassFilter(Div.class);
			NodeFilter divAttNF = new HasAttributeFilter("class","cover");
			NodeFilter filter = new AndFilter(new NodeFilter[]{divNF, divAttNF});
			
			NodeList list=parser.extractAllNodesThatMatch(filter);
			for(int i=0;i<list.size();i++)
			{
				Tag tag = (Tag)list.elementAt(i);
				if(tag instanceof Div)
				{
					 String str=tag.toHtml();
					 int temp=str.indexOf("src")+5;
					 str=str.substring(temp, str.indexOf(">",temp)-3);
					 String imgUrl=str.replace("thumb", "photo");
					 
			    	 if(imgUrl != null && URLUtil.getUrlType(imgUrl)== Page.IMAGE){
			    		  set.add(imgUrl);
				         // System.out.println("图片链接:"+ imgUrl);
			    		}else{
			    			System.out.println("error from ParseDouBan.java");
			    	}
				}
			}
		
//			for(Iterator it=set.iterator();it.hasNext();)
//			{
//				System.out.println(it.next());
//			}
				
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return set;	
	}
	/**
	 * 功能：获取电影预告片链接集合
	 * @author XiaoRui
	 */	                
    public static HashSet<String> videoUrlFilter(String url)
    {
    	HashSet<String> set = new HashSet<String>();
    	try{
    		Parser parser=new Parser();
    		parser.setURL(url);
    		parser.setEncoding(parser.getEncoding());
    		
    		NodeFilter videoFilter = new HasAttributeFilter("class","pr-video");

    		NodeList list = parser.extractAllNodesThatMatch(videoFilter);
    	
    		for(int i=0;i<list.size() && i<5; i++)      //最多只取5个预告片的链接
    		{
    			Tag tag=(Tag) list.elementAt(i);
    			
    			if("pr-video".equalsIgnoreCase(tag.getAttribute("class")))
    			{
    				String str=tag.getAttribute("href");
				    if(str!=null)
				    {
				    	set.add(str);
				    	//System.out.println("视频链接："+str);
				    }
    			}
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return set;
    } 
	
    /**
	 * 回访已爬过的链接.用于爬虫更新机制
	 * @param url
	 * @return
     * @throws Exception 
	 */
	public static Movie filter(Movie movie) throws Exception {
		Movie newMovie = null;
		HashSet<String> imgUrlSet=new HashSet<String>();
		HashSet<String> videoUrlSet=new HashSet<String>();
		DefaultFetcher fetcher = new DefaultFetcher();
		try{
			Parser parser = new Parser();
			parser.setURL(movie.getUrl());
			parser.setEncoding(parser.getEncoding());
			
			//电影的海报图片地址
			NodeFilter imgNF=new HasAttributeFilter("id","mainpic");
			
			//电影的预告片地址
			NodeFilter videoNF = new HasAttributeFilter("id","related-pic");
			
			//电影的评分
			NodeFilter DivNF = new NodeClassFilter(Div.class);
		    NodeFilter DivAttNF = new HasAttributeFilter("id","interest_sectl");
		    NodeFilter DivAF = new AndFilter(new NodeFilter[]{DivNF, DivAttNF});
		    
		    OrFilter filter = new OrFilter(new NodeFilter[]{DivAF,imgNF,videoNF});
		    NodeList list=parser.extractAllNodesThatMatch(filter);
	
		    newMovie = new Movie();
		    newMovie.setId(movie.getId());
		    for(int i=0;i<list.size();i++)
		    {
                Tag tag = (Tag) list.elementAt(i);
                
		    	if("interest_sectl".equalsIgnoreCase(tag.getAttribute("id")))  //获取电影的评分
				 {
					 String str=tag.toPlainTextString();
					 str=str.replaceAll(" ", "").replaceAll("\\n", "").trim();
					 newMovie.setScore(ScoreInfo(str));
					// System.out.println("电影评分"+'\n'+ScoreInfo(str));
				 } else if(movie.getImgSet().size() < 5 && "mainpic".equalsIgnoreCase(tag.getAttribute("id")))     //更新电影的海报图片链接地址
				 {
					 String str=tag.toHtml();
					 int temp=str.indexOf("href")+6;
					 str=str.substring(temp, str.indexOf("title",temp)-2);
					 imgUrlSet=imgUrlFilter(str);

					 if(imgUrlSet.size()!=0){
			    			imgUrlSet.removeAll(movie.getImgUrlSet());
			    			newMovie.setImgUrlSet(imgUrlSet);//移除重复的海报图片链接，获取新的海报图片链接
				    	}
				 }else if("related-pic".equalsIgnoreCase(tag.getAttribute("id")))    //更新电影的预告片链接
				 {
					 String str = tag.toHtml();
					 int temp=str.indexOf("href")+6;
					 str = str.substring(temp, str.indexOf(">", temp)-1);
					 videoUrlSet = videoUrlFilter(str);
					 if(videoUrlSet.size()!=0){
		    				videoUrlSet.removeAll(movie.getVideoUrlSet());
		    				newMovie.setVideoUrlSet(videoUrlSet);
		    			}
				 }
		    }
		}catch(Exception e)
		    {
		    	System.out.println(newMovie.getUrl());
		    	e.printStackTrace();
		    }
		if(newMovie.getImgSet().size()>0){
            fetcher.setUpdateFlag(true);
			fetcher.downloadImage(newMovie);   //在运行更新线程时，需要设置默认配置文件，使更新的海报图片下载到正确的位置
		}
		return newMovie;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//String url="http://movie.douban.com/subject/24843198/?from=playing_poster";
        String url="http://movie.douban.com/subject/4917726/";
		Movie movie=new Movie();
	    ParseDouBan.filter(url,movie);
		System.out.println(movie.toString());
	}

}
