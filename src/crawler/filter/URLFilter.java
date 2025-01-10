package crawler.filter;

import java.util.List;

import crawler.config.Config;
import crawler.url.URLUtil;

public class URLFilter implements IFilter {

	public static boolean CHECKHOST = Config.getInstance().isCheckhost(); //是否指定爬行范围
	public static List<String> allowDomain = null;
	
	// 可以通过正则表达式实现下面对子域名的过滤
	// 个人对正则不熟，就不实现了
	
	// filtering strings of all sites
	public final static String[] strFilter = {
		"login","signin","register","error","passport","help",
	};
	
	public static String[] doubanFilter = {
		"/subject_search","/amazon_search","/search","/group/search","/forum/","/new_subject",
		"/service/iframe","/j","/link2/","/recommend/","/trailer/","tv","comments","/trailers",
		"/review/","/questions","doulists","/trailer","/wishes","/all_photos","/collections","/reviews",
		"/photos","/ticket/mine","/discussion","/cinema","/mupload","/question","/awards"
	};
	
	//filtering strings of m1905
	public static String[] m1905Filter ={
		"#","comment","festival","vod","news","about","video","yx","vip","game",
		"http://hd.","newgallery","cctv6","http://index.","http://d.","http://event.",
		"http://bbs.","http://blog.","http://www.1905.com/film/","special",".shtml"
	};
	
	static{
		//是否限制只爬行某些域名主机
		allowDomain = Config.getInstance().getAllowDomain();
		if(CHECKHOST && (allowDomain == null || allowDomain.isEmpty())) {
			System.err.println("指定爬行主机,但没有指定域名范围,程序将强行退出!");
			System.exit(-1);
		}
	}
	
	
	/*
	 * Filter spam links
	 * If it is, return ture; otherwise false
	 */
	public boolean isFilterUrl(String url, String hostdomain) {
			
		// 1. domain filter
		if(!(CHECKHOST && allowDomain.contains(hostdomain))){
			//System.out.println("filter by domain: " + url);
			return true;
		}
		
		// 2. keyword filter
		int size = strFilter.length;
		for(int i = 0; i < size; i++) {
			if( url.indexOf(strFilter[i]) != -1) {
				//System.out.println("filter url for word: '" + strFilter[i] + "' at url:" + url);
				return true;
			}
		}

		// filter non-text url(image url)
		
		// 3. particular site filter

       if(hostdomain.equals("1905.com")){
			if(URLFilter.m1905FilterUrl(url)){
				return true;
			}
		}else if(hostdomain.equals("douban.com")){
			
			if(URLFilter.doubanFilterUrl(url)){
				return true;
			}
		}
		
		 return false;
	}
	
	public static boolean doubanFilterUrl(String url){
		
	    String host = URLUtil.getHost(url);
	    if(!host.equals("movie.douban.com"))
           return true;		
		
		int size = doubanFilter.length;
		for(int i = 0; i < size; i++) {
			if( url.indexOf(doubanFilter[i]) != -1) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean m1905FilterUrl(String url){
		
		String hostName = URLUtil.getHostDomainName(url);
		if(hostName.indexOf("1905.com/mdb/film/") == -1)
			return true;
		
		int size= m1905Filter.length;
		for(int i = 0; i < size; i++) {
			if( url.indexOf(m1905Filter[i]) != -1) {
				return true;
			}
		}
		return false;
	}
}