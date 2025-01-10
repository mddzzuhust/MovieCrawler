package crawler.feeder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.logging.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import crawler.client.Client;
import crawler.client.CrawlerClient;
import crawler.config.Config;
import crawler.crawler.Crawler;
import crawler.dispatcher.URLDispatcher;
import crawler.io.BDBURLProvider;
import crawler.io.FileURLProvider;
import crawler.io.IURLProvider;

public class Task extends TimerTask {

	//暂时不用优先级队列，因为我们要做的是收集最新的电影，而不是所有的电影
//	static Comparator<String> OrderUrl =  new Comparator<String>(){  //比较权值函数
//        public int compare(String o1, String o2) {  
//            // TODO Auto-generated method stub  
//            char numbera = o1.charAt(0);  
//            char numberb = o2.charAt(0);  
//            if(numberb > numbera)  
//            {  
//                return 1;  
//            }  
//            else if(numberb<numbera)  
//            {  
//                return -1;  
//            }  
//            else  
//            {  
//                return 0;  
//            }  
//        }       
//    };  
	public static URLDispatcher urlDispatcher;          //任务分发类
	public static Thread dispatcherThread;              //任务分发线程
	
	public static List<String> initialUrls;				//初始URL集合
	public static Queue<String> urlsToCrawl;			// 待爬URL集合
	//public static PriorityBlockingQueue<String> urlsToCrawl1;   //优先级队列，待爬URL集合
	public static Queue<String> urlsToCache;			// 待缓存URL集合(缓存到磁盘)
	//public static PriorityBlockingQueue<String> urlsToCache1;  //优先级队列 ，待缓存URL集合(缓存到磁盘)
	public static Queue<String> urlsToTest;				// 待处理URL集合(处理完就加入 urlsToCache)
	//public static PriorityBlockingQueue<String> urlsToTest1; //优先级队列，待处理URL集合
	public static List<Queue<String>> urlsToDispatch;	// 待分发URL集合
	public static List<Queue<String>> urlsCrawled;		// 已爬URL集合
	public static Queue<String> urlsError;				// 异常URL集合
	
	public static int updatedErrorUrls = 0;  //已向服务端提交的错误URL数目
	public static HashSet<Integer> updateSet = new HashSet<Integer>();
	
	public static IURLProvider urlProvider = new FileURLProvider();  // 本地磁盘缓存
	public static BDBURLProvider urlProvider2 = null;  // Berkeley DB缓存
	
	public static int MIN_URL_TO_DISPATCH = 1000;  //当待分发URL大于此数时,通知分发线程工作
	
	//记录拒绝爬行的URL(后缀名为rar,zip,exe等)数目.暂时没用
	public static AtomicLong dropedUrlSize = new AtomicLong(0);
	public static AtomicLong crawledImages = new AtomicLong(0);
	
	private static FileHandler fhandler;
	public static Logger logger;
	
	public static boolean useBDB;
	
	static {
		urlDispatcher = new URLDispatcher();
		
		initialUrls = new ArrayList<String>();
		urlsToCrawl = new ConcurrentLinkedQueue<String>();
	   // urlsToCrawl1 = new PriorityBlockingQueue<String>(11,OrderUrl);
		urlsToCache = new ConcurrentLinkedQueue<String>();
	   // urlsToCache1 = new PriorityBlockingQueue<String>(11,OrderUrl);
		urlsToTest = new ConcurrentLinkedQueue<String>(); 	
	   // urlsToTest1 =  new PriorityBlockingQueue<String>(11,OrderUrl);
		urlsError = new ConcurrentLinkedQueue<String>();
		
		useBDB = Config.getInstance().isUseBDB();
		if(useBDB)
			urlProvider2 = new BDBURLProvider();
			
		try {
			fhandler = new FileHandler("crawler.log");
		} catch (SecurityException e) {
			System.out.println("创建日志失败!");
		} catch (IOException e) {		}
		
		logger = Logger.getLogger("idc.crawler"); //构造一个记录器
		logger.addHandler(fhandler); //为记录器添加一个FileHandler
	}
	
	public static void init(int totalHashNum) {
		urlsToDispatch = new ArrayList<Queue<String>>();
		for(int i =  0; i < totalHashNum; i++) { //为每一个哈希号建立一个数组保存待分发URL列表.
			urlsToDispatch.add(new ConcurrentLinkedQueue<String>());
		}
		urlsCrawled = new ArrayList<Queue<String>>();
		for(int i =  0; i < totalHashNum; i++) { //为每一个哈希号建立一个数组保存已爬行URL列表.
			urlsCrawled.add(new ConcurrentLinkedQueue<String>());
		}
	}
	
	public void run() {
		if(useBDB){
			if (Task.urlsToCrawl.size() < IURLProvider.savePerSize / 2)
				urlProvider2.loadToCrawlUrls();

			while (Task.urlsToCache.size() > 2 * IURLProvider.savePerSize)
				urlProvider2.saveToCrawlUrls();

		} else {
			if (Task.urlsToCrawl.size() < IURLProvider.savePerSize / 2)
				urlProvider.loadToCrawlUrls();

			while (Task.urlsToCache.size() > 2 * IURLProvider.savePerSize)
				urlProvider.saveToCrawlUrls();
		}
		
		/* 待爬行URL数太少,可能是判重线程过于迟缓,尝试提高其优先级 */
		if (Task.urlsToCrawl.size() < 100 * CrawlerClient.threadMap.size()) {
			int priority = CrawlerClient.feedThread.getPriority();
			if(priority < Thread.MAX_PRIORITY) {
				CrawlerClient.feedThread.setPriority(priority + 1);
			}
		}
		
		while (Task.urlsError.size() > IURLProvider.savePerSize) {
			urlProvider.saveErrorUrls();
		}
		
		while (Task.getUrlToDispatchSize() > 2 * IURLProvider.savePerSize) {
			urlProvider.saveToDispatchUrls();
		}
		if (Task.getUrlToDispatchSize() < IURLProvider.savePerSize / 2) {
			urlProvider.loadToDispatchUrls();
		}
		if (Task.getUrlToDispatchSize() > MIN_URL_TO_DISPATCH) {
			if (Task.dispatcherThread == null || !Task.dispatcherThread.isAlive()) {
				Task.dispatcherThread = new Thread(Task.urlDispatcher);
				Task.dispatcherThread.start();
				System.out.println("重新启动分发线程.");
			} else synchronized(URLDispatcher.lock) {
				System.out.println("分发线程存活:" + Task.dispatcherThread.getState());
				URLDispatcher.lock.notify(); //通知分发线程开始工作
			}
		}
		
		while (Task.getCrawledUrlSize() > 2 * IURLProvider.savePerSize) {
			urlProvider.saveCrawledUrls();
		}
		
		Task.checkThread();
		Task.showStatus();
	}
	
	public static IURLProvider getURLProvider() {
		return urlProvider;
	}
	
	public static int getCrawledUrlSize(){
		int totalSize = 0;
		int size = Task.urlsCrawled.size();
		for (int i = 0; i < size; i++) {
			totalSize += Task.urlsCrawled.get(i).size();
		}
		
		return totalSize;
	}
	
	public static int getUrlToDispatchSize() {
		int totalSize = 0;
		int size = Task.urlsToDispatch.size();
		for (int i = 0; i < size; i++) {
			totalSize += Task.urlsToDispatch.get(i).size();
		}
		
		return totalSize;
	}
	
	public static void showStatus() {
		String time = Task.getTime();
		int total = getCrawledUrlSize();
		String message;
		if(useBDB)
			message = "待爬链接:(" + urlProvider2.getSavedUrlsToCrawlSize() + "+" + Task.urlsToCache.size() + ")个,";
		else 
			message = "待爬链接:(" + urlProvider.getSavedUrlsToCrawlSize() + "+" + Task.urlsToCache.size() + ")个,";
		
		message = "[" + time + "]" + " - " + message +
			"正爬链接:(" + Task.urlsToCrawl.size() + ")个," + 
 	   		"待判重:(" + Task.urlsToTest.size() + ")个," + 
 	   		"待分发:(" + urlProvider.getSavedUrlsToDispatchSize() + "+" + Task.getUrlToDispatchSize() + ")个," + 
 	   		"已爬链接:(" + urlProvider.getSavedUrlsCrawledSize() + "+" + total + ")个," + 
 	   		"错误数:(" + urlProvider.getSavedUrlsErrorSize() + "+" + Task.urlsError.size() + ")个," + 
 	   		"存活线程:" + Crawler.instance.get();
		String uiMessage = "[" + time + "]" + " - " + 
					"抓取地址: " + (urlProvider.getSavedUrlsCrawledSize() + total) +  
					" 个,爬取图片: " + Task.crawledImages.get() + ", urlsToCrawl:" + Task.urlsToCrawl.size();
		
		Client.showMessage(uiMessage);
		System.out.println(message);
	}
	
	public static String getTime() {
		Calendar calendar = Calendar.getInstance();
		String h = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		String m = Integer.toString(calendar.get(Calendar.MINUTE));
		String s = Integer.toString(calendar.get(Calendar.SECOND));
		
		h = (h.length() == 1 ? "0" + h : h);
		m = (m.length() == 1 ? "0" + m : m);
		s = (s.length() == 1 ? "0" + s : s);
		String time = h + ":" + m + ":" + s;
		
		return time;
	}
	
	public static void checkThread() {
		if (!CrawlerClient.running)
			return;
		Thread eThread = null;
		Iterator<?> it = CrawlerClient.threadMap.entrySet().iterator();
		java.util.Map.Entry<?, ?> entry = null;
		while (it.hasNext()){
			entry = (java.util.Map.Entry<?, ?>)it.next();
			eThread = (Thread)entry.getValue();
			if (eThread == null || !eThread.isAlive()) {
				System.out.println("线程" + entry.getKey() + "异常退出,将被重新启动.");
				String threadname = eThread.getName();
				eThread = new Thread(new Crawler(),threadname);
				eThread.setName(threadname);
				Crawler.instance.decrementAndGet();
				CrawlerClient.threadMap.remove(threadname);
				CrawlerClient.threadMap.put(threadname,eThread);
				eThread.start();
				
				it = CrawlerClient.threadMap.entrySet().iterator();
			}
		}
		
		if (CrawlerClient.feedThread == null || !CrawlerClient.feedThread.isAlive()) {
			CrawlerClient.feedThread = new Thread(new FeedThread(),"Thread#" + 0);
			CrawlerClient.feedThread.start();
			System.out.println("重新启动判重线程.");
		}
	}
	
	public static void closeBDB(){
		if(useBDB)
			urlProvider2.closeBDB();
	}
	
	//测试
	public static void main(String[] args) throws IOException {
//	    Task.urlList.add("http://www.soso.com");
//		Task.urlList.add("http://www.baidu.com");
//		Task.urlList.add("http://www.google.com.hk");
//		Task.savePerSize = 1;
//		Task.storeUrlsToFile();
//		System.out.println("after store, url size:" + Task.urlList.size());
//		System.out.println("toWrite:" + Task.toWriteUrlFileIndex + ", toRead:" + Task.toReadUrlFileIndex);
//		Task.savePerSize = Task.urlList.size();
//		Task.loadUrlsFromFile();
//		System.out.println("after read, url size:" + Task.urlList.size());
//		System.out.println("toWrite:" + Task.toWriteUrlFileIndex + ", toRead:" + Task.toReadUrlFileIndex);
	}
}
