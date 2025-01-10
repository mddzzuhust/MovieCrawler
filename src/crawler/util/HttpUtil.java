package crawler.util;


import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import crawler.config.Config;
import crawler.proxy.ProxyPool;

public class HttpUtil {
	
	static Config config = Config.getInstance();
	
    /**
     * @param url   打开连接的URL地址
     * @param proxy 使用代理打开,为空则表示不使用
     */
    static int connectTimeOut = config.getConnectTimeOut();
    static int readTimeOut = config.getReadTimeOut();
    final public static HttpURLConnection getConnection(URL url, Proxy proxy) throws Exception {
		
    	HttpURLConnection huc = null;
    	if(proxy != null)
    		huc = (HttpURLConnection)url.openConnection(proxy);
    	else
    		huc = (HttpURLConnection)url.openConnection();
        huc.setRequestProperty("Accept", "*/*");
        huc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)");
        
//        HttpURLConnection.setFollowRedirects(false);
//        huc.setAllowUserInteraction(false);
        huc.setConnectTimeout(connectTimeOut); //连接超时:30秒
        huc.setReadTimeout(readTimeOut); //读取超时:30秒
        
        return huc;
    }
    
    final static public HttpURLConnection getConnection(URL url) throws Exception {
		
    	return getConnection(url, ProxyPool.getProxy());
        
    }

	final public static HttpURLConnection getConnection(String url, Proxy proxy) throws Exception {
		
        return getConnection(new URL(url), proxy);
        
    }
	
	final static public HttpURLConnection getConnection(String url) throws Exception {
		
   		return getConnection(new URL(url));
        
    }
	
}
