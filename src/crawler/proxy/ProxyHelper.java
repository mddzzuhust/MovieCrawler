package crawler.proxy;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import crawler.config.Config;
import crawler.util.HttpUtil;

public class ProxyHelper {
	private static StringBuilder html = null;
	private static Config config = Config.getInstance();
	
	private static int MAX_CHARSET_LENGTH = 11; /* 这是我认为的最长的编码字符串(gbk,gb2312,utf-8,iso-8859-1等)的长度 */
	private static int NORMAL_CONTENT_CHARS_LENGTH = 15000;  //15000个字符,大约30KB
	//private static int MAX_PAGE_SIZE = config.getMaxPageSize(); /* 网页内容大于该值,则丢弃 */
	//private static int MIN_PAGE_SIZE = config.getMinPageSize(); /* 网页内容小于该值,则丢弃 */
	
    /** 
     * 根据URL得到html代码字符串 
     * @return html代码 
     * @throws Exception 
     */
    public static String urlToHTML(String url) throws Exception {
    	URL u = new URL(url);
    	HttpURLConnection conn = HttpUtil.getConnection(u);
    	
    	html = new StringBuilder(NORMAL_CONTENT_CHARS_LENGTH);  //15000个字符,大约30KB
    	BufferedInputStream in = null;
    	byte[] temp = new byte[2048];
    	boolean hasCharset = false;
    	String charset = "utf-8";
    	try {
            String contentType = conn.getContentType();
            if(contentType != null && contentType.contains("charset")) {
            	charset = getCharset(contentType);
            	if(charset == null || charset.equals(""))
            		charset = "utf-8";
            	hasCharset = true;
            }
            in = new BufferedInputStream(conn.getInputStream());
            String inputLine;
            int bytesRead = 0;
            int totalBytes = 0;
            while ((bytesRead = in.read(temp)) >= 0) {
            	totalBytes += bytesRead;
            	/*if(totalBytes > MAX_PAGE_SIZE) {
            		return null;
            	}*/
                inputLine = new String(temp, 0, bytesRead, charset);
                if(!hasCharset) {
    				String charsetName = getCharset(inputLine);
    				if(charsetName != null) {
    					hasCharset = true; // 不管如何,后面不再检测.若失败,则使用默认编码.
    					if(!charset.equalsIgnoreCase(charsetName)) {
    						charset = charsetName;
    						inputLine = new String(temp, 0, bytesRead, charset);
    					}
    				} else {
    					if(totalBytes > 1024 || inputLine.indexOf("</head>") != -1)
    						hasCharset = true; //已经超过头部范围了,后面不可能有正确的编码信息,停止检测
    				}
                }
                html.append(inputLine);
            }
            /*if(totalBytes < MIN_PAGE_SIZE)
            	return null;*/
            return html.toString();
    	} finally {
    		if(in != null)
    			in.close();
    		if(conn != null)
    			conn.disconnect();
    	}
    }
    
    /**
     * 提取页面编码.我自己处理字符串,不用正则式,因为我测试过正则提取的速度不如它
     * @author liuxue
     * @param str 包含"charset"的字符串
     * @return    字符编码charset
     */
    private static String getCharset(String str) {
    	String charset = null;
		int pos = str.indexOf("charset");
		if(pos == -1)
			pos = str.indexOf("CHARSET");
		if(pos != -1) {
			pos = str.indexOf('=',pos+7);
			pos++;
			while(str.charAt(pos) == '"' ||	str.charAt(pos) == '\'' || str.charAt(pos) == ' ') 
				pos++;
			int pos2 = str.indexOf('"', pos);
			if (pos2 != -1 && pos2 - pos < MAX_CHARSET_LENGTH) {
				charset = str.substring(pos, pos2).trim();
			} else if ((pos2 = str.indexOf('\'', pos)) != -1 && pos2 - pos < MAX_CHARSET_LENGTH) {
				charset = str.substring(pos, pos2).trim();
			} else if ((pos2 = str.indexOf(' ', pos)) != -1 && pos2 - pos < MAX_CHARSET_LENGTH) {
				charset = str.substring(pos, pos2).trim();
			} else if ((pos2 = str.indexOf('/', pos)) != -1 && pos2 - pos < MAX_CHARSET_LENGTH) {
				charset = str.substring(pos, pos2).trim();
			} else if((pos2 = str.length()) - pos < MAX_CHARSET_LENGTH) {
				charset = str.substring(pos).trim();
			}
			/**
			 * 根据"《自己动手写搜索引擎》(罗钢著)"这本书的教唆,尝试把gb2312编码也当做gbk编码
			 * 但是我不知道这样有什么好处. 麻烦,我先不用了
			 */
//			if(charset != null && charset.contains("gb2312"))
//				charset = "gbk";
			if(charset != null && !java.nio.charset.Charset.isSupported(charset))
				charset = "utf-8";
		}
		return charset;
    }
    
	
	public static void main(String[] args){
		
		//String url = "http://www.cnproxy.com/proxy1.html";
		//String temp = ProxyHelper.urlToHTML(url);
		//System.out.println(temp);
	}
}
