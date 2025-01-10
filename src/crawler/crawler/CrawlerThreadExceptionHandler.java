package crawler.crawler;


import java.lang.Thread.UncaughtExceptionHandler;

import crawler.client.CrawlerClient;
import crawler.feeder.Task;

public class CrawlerThreadExceptionHandler implements UncaughtExceptionHandler {

	private Runnable crawler;

	public CrawlerThreadExceptionHandler(Runnable crawler) {
		this.crawler = crawler;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		// TODO Auto-generated method stub
		Thread newThread = new Thread(crawler, t.getName());
		CrawlerClient.threadMap.put(t.getName(), newThread);
		newThread.start();
		String error = "[" + Task.getTime() + "] - Thread -[" + t.getName()
				+ "]- exit with exception: " + e.getMessage()
				+ ", restart it now ...";
		System.err.println(error);
	}

}
