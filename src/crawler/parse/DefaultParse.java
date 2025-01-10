package crawler.parse;

import java.util.HashSet;

import crawler.entity.Movie;
import crawler.url.URLUtil;

/**
 * 默认解析类.分发具体解析任务到对应的网站解析类
 * @author xiaoRui
 *
 */
public class DefaultParse {
	
	/**
	 * 调用对应的电影网站解析类
	 * @param url
	 * @param movie
	 * @return
	 */
	public static HashSet<String> Parse(String url,Movie movie){
		String domain = URLUtil.getDomain(url);

	//	System.out.println("域名："+domain);
		if(domain.equals("douban.com")){
			return ParseDouBan.filter(url, movie);
		}
		else if(domain.equals("1905.com"))
		{
			return ParseM1905.filter(url, movie);
		}
		else 
			return null;
	}
	
	
	public static Movie Parse(Movie movie) throws Exception{
        String domain = URLUtil.getDomain(movie.getUrl());
		
		if(domain.equals("m1905.com"))
			return ParseM1905.filter(movie);
		else if(domain.equals("douban.com"))
			return ParseDouBan.filter(movie);
		else 
			return null;
		
	}
}
