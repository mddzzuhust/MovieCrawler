package crawler.url;

/**
 * 修改链接为最简形式,避免同一页面也不同链接而重复下载
 * @author fugui
 *
 */
public class ShortenURL {

	// the sites which need shorten
	public static String[] ShortenDomain = {"amazon.cn", "dangdang.com","mbaobao.com","taobao.com","tmall.com","vip.com","yixun.com","douban.com"};
	
	public static String DefaultShorten(String url, int index){
		
		switch(index){
		case 0:
			url = ShortenAmazonURL(url);
			break;
		case 1:
			url = ShortenDDURL(url);
			break;
		case 2:
			url = ShortenMBBURL(url);
			break;
		case 3:
			url = ShortenTBURL(url);
			break;
		case 4:
			url = ShortenTMallURL(url);
			break;
		case 5:
			url = ShortenVIPURL(url);
		case 6:
			url = ShortenYXURL(url);
		case 7:
			url = ShortenDBURL(url);
		default:
			break;
		}
		
		return url;
	}

	public static String ShortenAmazonURL(String url){
		int index = url.indexOf("/dp/");
		if(index != -1){
			int pos = url.indexOf("#", index);
			if(pos != -1)
				url = "http://www.amazon.cn" + url.substring(index, pos);
			else
				url = "http://www.amazon.cn" + url.substring(index);
		}
		
		return url;
	}
	
	public static String ShortenDDURL(String url){
		
		url = url.toLowerCase();
		
		int index = url.indexOf(".html");
		String extend = "product_id=";
		if(index != -1){
			url = url.substring(0, index + 5);
			
		} else if(url.contains(extend)){
			// "http://product.dangdang.com/".length() = 28
			// "http://product.dangdang.com/product.aspx?product_id=".length() = 52
			int pos = url.indexOf('#', 52);
			
			if(pos != -1) 
				url = url.substring(0, 28) + url.substring(52, pos) + ".html";  
			else{
				pos = url.indexOf('&', 52);
				if(pos != -1)
					url = url.substring(0, 28) + url.substring(52, pos) + ".html";
				else
					url = url.substring(0, 28) + url.substring(52) + ".html";
			}
		}
		return url;
	}
	
	private static String ShortenMBBURL(String url) {
		if(url.contains("/item/")){
			// "http://www.mbaobao.com/item/".length() = 28
			int index = url.indexOf('?', 28);
			if(index != -1){
				url = url.substring(0, index);
			}/* else if((index = url.indexOf("?w_l")) != -1){
				url = url.substring(0, index);
			} else if((index = url.indexOf("?s_l")) != -1){
				url = url.substring(0, index);
			} else if((index = url.indexOf("?c_l")) != -1){
				url = url.substring(0, index);
			}*/
		}
		return url;
	}
	
	public static String ShortenTBURL(String url){
		int index = url.indexOf("&on_comment=1");
		if(index != -1){
			url = url.substring(0, index);
		}
		return url;
	}
	
	public static String ShortenTMallURL(String url){
		int index = url.indexOf("item.htm?id=");
		if(index != -1){
			int pos = url.indexOf('&', index);
			if(pos != -1)
				url = url.substring(0, pos);
		}
		return url;
	}
	
	public static String ShortenVIPURL(String url){
		
		if(url.endsWith("#"))
			url = url.substring(0, url.length() - 1);

		return url;
	}
	
	public static String ShortenYXURL(String url){
		
		if(url.endsWith("#"))
			url = url.substring(0, url.length() - 1);
		else if(url.endsWith("#review_box")){
			url = url.substring(0, url.length() - 11);
		}

		return url;
	}
	
	public static String ShortenDBURL(String url){
		
		int index = url.indexOf("?");
		if(index != -1)
			url = url.substring(0, index);
		
		if(url.indexOf("#search") != -1)
			return url;
		index = url.indexOf("#");
		if(index != -1)
			url = url.substring(0, index);
		return url;
	}
	
	public static void main(String[] args){
		String url = "http://movie.douban.com/coming#more";
			//	"http://movie.douban.com/subject/10807916/#";
		//"http://item.yixun.com/item-2416106348.html#review_box";
		//"http://product.dangdang.com/product.aspx?product_id=1014250921#ddclick?act=click&amp;pos=1014250921_6_1_q&amp;cat=&amp;key=zefer&amp;qinfo=59_1_48&amp;pinfo=&amp;minfo=&amp;ninfo=&amp;custid=&amp;permid=20121116111105281209154620624910833&amp;ref=http%3a%2f%2fsearch.dangdang.com%2f%3fkey%3dzefer%26page_index%3d2&amp;rcount=&amp;type=&amp;t=1366873588000";
		
		//System.out.println(ShortenYXURL(url));
		//System.out.println(ShortenMBBURL(url));
		//System.out.println("http://www.mbaobao.com/item/".length());
		System.out.println(ShortenDBURL(url));
	}
}
