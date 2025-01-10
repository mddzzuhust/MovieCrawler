package crawler.crawler;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import crawler.client.CrawlerClient;
import crawler.config.Config;
import crawler.feeder.Task;
import crawler.fetcher.DefaultFetcher;
import crawler.util.IPUtil;
import crawler.url.URLUtil;


/**
 * 抓取线程--非常重要,努力保证它的稳定性,不能因为某个异常而结束!!!
 * @author liuxue
 */
public class Crawler implements Runnable {
	
	private volatile boolean running = true; //运行标志,控制线程是否结束
	
	public static AtomicInteger instance = new AtomicInteger(0); //线程实例数
	
	public static String rootAddress = ""; //本机IP地址+共享目录
	
	public static String rootPath = ""; //媒体文件保存根目录
	
	static {
		String ipaddress = IPUtil.getLocalAddress();
		if(ipaddress == null || ipaddress.isEmpty()) {
			System.err.println("无法获取本机IP地址,请检查网络设置.");
			System.exit(-1);
		}
		String file_separator = System.getProperty("file.separator");
		// rootAddress = "\\211.69.205.242\IDCMedia"
		rootAddress = /*file_separator + file_separator +*/ ipaddress
				/*+ file_separator + "IDCMedia"*/;
		rootPath = Config.getInstance().getImageSavePath();
		
		// use for test site: http://ir.nist.gov
		//java.net.Authenticator.setDefault(new idc.crawler.util.AuthenticatorEntity());
	}
	
	private Random random = new Random();
	private String separator = System.getProperty("line.separator");
	
	public void startCrawl() {
		this.running = true;
	}
	
	public void stopCrawl() {
		running = false;
	}
	
	public boolean isCrawling() {
		return running;
	}
	
	public static void setRootPath(String path) {
		if(path != null && !path.isEmpty())
			Crawler.rootPath = path;
		else
			Crawler.rootPath = System.getProperty("user.dir");
		java.io.File file = new java.io.File(Crawler.rootPath);
		if(!file.exists() || !file.isDirectory())
			file.mkdirs();
	}
	
	public String dequeueURL() {
		//System.out.println(Task.urlsToCrawl.size());
		return Task.urlsToCrawl.poll();
	}
	
	public void run() {
		Crawler.instance.incrementAndGet();
		DefaultFetcher fetcher = new DefaultFetcher();
		String url = null;
		int urlHash = 0;
		
		for(; running ;) {
			try {
				while(running) {
					
					Thread.sleep(5000);  //爬行豆瓣电影网站的时候，为了避免爬虫被屏00000蔽，需要暂停5s对网站进行爬行
					url = dequeueURL();
					if(url == null) {
						Thread.sleep(random.nextInt(10) * 1000);
						continue;
					}
				
					urlHash = URLUtil.getHashcode(url);
					if(!CrawlerClient.hashCode.contains(urlHash)) {
						//该URL不属于自己的任务范围
						System.out.println("执行链接分发");
						Task.urlsToDispatch.get(urlHash).add(url);             
						continue;
					}
					
					if(fetcher.fetch(url)) {  //成功抓取该URL
						Task.urlsCrawled.get(urlHash).add(url);
					} else {  //失败
						Task.urlsError.add(url);
					}
				}
			} catch (Exception e) {
				String message = e.getMessage();
				if(message != null && message.contains("Address already in use")) {
					Task.urlsToTest.add(url);
					continue;
				}
				Task.urlsError.add("#Exception:" + message + separator + url);
				if (CrawlerClient.debug) {
					System.out.println("线程" + Thread.currentThread().getName()
							+ "处理:" + url + "异常!");
					e.printStackTrace();
				}
			}
		} // end for
		int crawlers = Crawler.instance.decrementAndGet();
		System.out.println("线程" + Thread.currentThread().getName() + "退出,当前仍有"
				+ crawlers + "个线程");
	}
}
