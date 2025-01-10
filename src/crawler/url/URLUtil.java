package crawler.url;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import crawler.client.CrawlerClient;
import crawler.config.Config;
import crawler.entity.Page;

public class URLUtil {

	public static final char[] ALLOWED_URI_CHARS;
	static Config config = Config.getInstance();
	static HashSet<String> imageType = config.getAllowImageType();
	
	static {
		ALLOWED_URI_CHARS = new char[] {
			'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'0','1','2','3','4','5','6','7','8','9',
			';','/','?',':','@','&','=','+','$','-','_','.','!','~','*','\'','(',')','%',
		};
		Arrays.sort(ALLOWED_URI_CHARS);
	}
	
	/**
	 * 判断URL类型
	 * @param url
	 * @return 普通网页:0 image:1
	 */
	public static int getUrlType(String url) {
		 
		int pos = url.lastIndexOf('.');
		if(url.length() - pos > 7)
			return 0;
		
		String extname = url.substring(pos + 1).toLowerCase();
		if(imageType.contains(extname))
			return Page.IMAGE;
		
		return 0;
	}
	
	/**
	 * 获取链接的后缀名. 主要是图片地址链接后缀
	 * @param url
	 * @return 后缀名. 如jpg/png等
	 */
	public static String getUrlSuffix(String url) {
		 
		int pos = url.lastIndexOf('.');
		if(pos == -1)
			return null;
		
		String suffix = url.substring(pos + 1).toLowerCase();
		if(suffix.length() > 4){
			// 易讯的图片链接地址比较特殊
			suffix = suffix.substring(0, suffix.lastIndexOf('/'));
		}
		return suffix;
	}
	
	/**
	 * 将URL规格化成文件名，处理"/", ":", ".", "\", "?", "|", "*";
	 * @param url 传进来的url
	 * @return
	 */
    public static String urlToFilename(String url) {
		url = url.replaceAll("/", "_").replaceAll(":", "_").replaceAll("\\.",
				"_").replaceAll("\\?", "_").replaceAll("\\|", "_").replaceAll(
				"\\*", "_").replaceAll("\\\\", "_");

		int index = url.lastIndexOf('_');
		if (url.length() - index > 5)
			return url;
		else
			return url.substring(0, index);
	}
    
    /**
	 * Returns the filename of an URL or null if the URL ends with an slash.
	 * 
	 *  http://server.com/a/foo.bin -> method returns "foo.bin"
	 *  http://server.com/a/ -> method returns null
	 *  http://server.com -> method returns null
	 *  
	 * @param pUrl
	 * @return The filename or null
	 */
	public static String getFilenameFromUrl(final java.net.URL pUrl) {
		
		String fullPath = pUrl.getFile();

		String filename = null;
		if(!fullPath.endsWith("/") && fullPath.lastIndexOf('/') != -1) {
			filename = fullPath.substring(pUrl.getPath().lastIndexOf('/') + 1);
		}
		
		return filename;
	}
	
    /**
     * 获取主机域名,不包括www.,mail.等
     * "http://ir.nist.gov" 返回 nist.gov
     * @param url String 主机url地址
     * @return String 主机域名
     */
    /* 用来匹配IP地址 */
	static final Pattern ipPattern = Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}$");
	public static String getHostDomainName(String urlHost) {
		if(ipPattern.matcher(urlHost).matches())
			return urlHost;
		
		int pos = urlHost.indexOf('.');
		//对形如6.cn这样的主机域名,不再切割(如切割会返回"cn"),直接返回
        if (pos != -1 && pos != urlHost.lastIndexOf('.')) {
            urlHost = urlHost.substring(pos + 1);
        }
        return urlHost;
    }
	
    /**
     * "http://ir.nist.gov/tv2007/soundandvision/tv7.sv.devel/"
     * return ir.nist.gov
     * @param url
     * @return
     */
	public static String getHost(String url) {
		try {
			return new java.net.URL(url).getHost().toLowerCase();
		} catch (MalformedURLException e) {
			return "";
		}
	}
	
	public static String getDomain(String url) {
		String urlHost = null;
		try {
			urlHost = new java.net.URL(url).getHost().toLowerCase();
		} catch (MalformedURLException e) {
			return "";
		}
		
		if(ipPattern.matcher(urlHost).matches())
			return urlHost;
		
		int pos = urlHost.indexOf('.');
        if (pos != -1 && pos != urlHost.lastIndexOf('.')) {
            urlHost = urlHost.substring(pos + 1);
        }
        return urlHost;
    }
	
	// get the depth('/') of the URL to determine whether is should be grabber.
	public static int getUrlDepth(String url) {
		int depth = 0;
		int pos = url.indexOf("://") + 3;
		int length = url.length();
		for(; pos < length; pos++) {
			if(url.charAt(pos) == '/')
				depth++;
		}
		return depth;
	}
	
	// get the separator char's number to determine whether is should be grabber.
	public static int getUrlSeparatorNum(String url) {
		char c;
		int separatorNum = 0;
		int pos = url.lastIndexOf('/') + 1;
		int length = url.length();
		for(; pos < length; pos++) {
			c = url.charAt(pos);
			if(c == '-' || c == '_' || c == '=')
				separatorNum++;
		}
		return separatorNum;
	}
	
	/**
	 * 将URL编码为UTF-8格式,处理URL的中文等问题
	 * @param url
	 * @return
	 */
	public static String toUTF8(String url) {
		char c;
		byte[] b;
		int length = url.length();
		StringBuilder sb = new StringBuilder(length);
		for(int i=0; i < length; i++) {
			c = url.charAt(i);
			if(Arrays.binarySearch(ALLOWED_URI_CHARS, c) > -1) {
				sb.append(c);
			} else {
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch(Exception ex) {
					b = new byte[0];
				}
				
				for(int j = 0; j < b.length; j++) {
					int k = b[j];
					if(k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k));
				}
			}
		}
		b = null;
		return sb.toString();
	}
	
	/**
	 * 对URL进行哈希操作
	 * @param url:URL地址
	 * @return URL的哈希值,注意这里只对主机名,如hust.edu.cn进行哈希操作
	 */
	public static int getHashcode(String url) {
		if(url == null)
			return 0;
		int n = 0;
		//byte[] b = new byte[4];
		
		/**
		 * 因为只爬电子商务网站，数量较少.对域名进行哈希不能获得很好的分配效果
		 * 负载均衡效果很差，故直接对链接进行哈希
		 */
		if(/*!FeedThread.CHECKHOST*/true) {
			//下面这个if块取主机名,如http://www.hust.edu.cn/index.html,则取hust.edu.cn
			int pos = url.lastIndexOf("/");
			if(pos != -1) {
				/*int pos2 = url.indexOf("/", pos);
				if(pos2 != -1)
					url = url.substring(pos + 1, pos2);*/
				url = url.substring(pos + 1);
			}
		}
		//System.out.println(url);
		/*int size = url.length();
		for(int i = 0; i < size; i++){
			b[i%4] = (byte)url.charAt(i);
		}
		n = ((((b[3] & 0xff) << 8 | (b[2] & 0xff)) << 8) | (b[1] & 0xff)) << 8 | (b[0] & 0xff);
		b = null;
		
		n = (n + CrawlerClient.totalHashNum) % CrawlerClient.totalHashNum;*/
		
		int size = url.length();
		int seed = 0;
		for(int i = 0; i < size; i++){
			seed = seed + url.charAt(i);
		}
		n = (seed + CrawlerClient.totalHashNum) % CrawlerClient.totalHashNum;
		//System.out.println("seed:" + seed);
		//java.util.Random r = new java.util.Random(seed);
		//n = r.nextInt();
		
		if(n >= 0 && n < CrawlerClient.totalHashNum)
			return n;
		else {
			if(CrawlerClient.debug)
				System.out.println("URL哈希出现异常值:" + n + ",在URL" + url);
			return 0;
		}
	}
	
	public static void main(String[] args) throws Exception {
        String url="http://www.m1905.com/mdb/film/2213789/";
	
        System.out.println(URLUtil.getHost(url));
        System.out.println(URLUtil.getDomain(url));
        System.out.println(URLUtil.getHostDomainName(url));
        System.out.println(URLUtil.getUrlSeparatorNum(url));
        System.out.println(URLUtil.getHashcode(url));
        
		
	}
}
