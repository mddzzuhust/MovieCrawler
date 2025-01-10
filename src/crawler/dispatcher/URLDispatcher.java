package crawler.dispatcher;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import crawler.client.CrawlerClient;
import crawler.feeder.Task;

/**
 * 将不属于本身任务的URL发送出去(到处理该URL的主机).无限循环处理,等待/唤醒方式.
 * @author Administrator
 *
 */
public class URLDispatcher implements Runnable {
	
	public final static Object lock = new Object();
	
	public void run() {

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Socket socket = null;
		PrintWriter writer = null;
		BufferedReader reader = null;
		ArrayList<String> tempUrl = new ArrayList<String>();
		int size = 0;
		
		Iterator<?> it = null;
		java.util.Map.Entry<?, ?> entry = null;
		List<?> tempHash = null;
		
		for( ; ; ) {
			
			it = CrawlerClient.clients.entrySet().iterator();
			while(it.hasNext()){
				tempUrl.clear();
				entry = (java.util.Map.Entry<?, ?>)it.next();
				tempHash = (ArrayList<?>)entry.getValue();
				for(int j = 0, m = Task.urlsToDispatch.size(); j < m; j++) {
					if(tempHash.contains(j)) {
						size = Task.urlsToDispatch.get(j).size();
						for(int k = size - 1; k >= 0 ; k--)
							tempUrl.add(Task.urlsToDispatch.get(j).poll());
					}
				}
				
				if(tempUrl.size() > 0) {
					if(entry.getKey().toString().equals(CrawlerClient.clientID + "")) {
						//自己的任务(可能在某一次任务调度后出现这样的情况).
						// Task.urlsToCrawl.addAll(tempUrl);
						Task.urlsToCache.addAll(tempUrl);
						System.out.println("URLDispatcher to Task.urlsToCache");
						continue;
					}
					
					String ip = ((ArrayList<?>)entry.getValue()).get(0).toString();
//					System.out.println("检测爬虫:" + entry.getKey() + ip);
					try {
						socket = new Socket();
//						socket.setSendBufferSize(1024);
				        socket.setTcpNoDelay(true);
				        socket.setSoTimeout(30*1000);
				        socket.connect(new InetSocketAddress(ip.substring(1),CrawlerClient.CLIENTPORT));
					} catch (Exception e) {
						System.out.println("无法连接爬虫:" + entry.getKey());
						try {
							Socket ssocket = new Socket(CrawlerClient.HOSTADDRESS,CrawlerClient.SERVERPORT);
							writer = new PrintWriter(ssocket.getOutputStream());
							writer.println("CRAWLEREXIT#" + entry.getKey());
							writer.flush();
							writer.close();
							ssocket.close();
							ssocket = null;
						} catch (Exception e1) {
							System.out.println("连接服务器失败!请检查网络,或联系管理员.");
						}
						continue;
					}
					
					try {
						writer = new PrintWriter(socket.getOutputStream());
						writer.println("URL_RECEIVE#" + entry.getKey().toString());
						writer.flush();
						reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//						System.out.println("发送URL_RECEIVE完成...");
						String msg = reader.readLine();
//						System.out.println("接收READY完成...");
						if(msg == null || !msg.startsWith("READY")) {
							System.out.println("目标爬虫无法正常接收!");
							writer.close();
							reader.close();
							socket.close();
							continue;
						}
						
						int send = 0;
//						System.out.println("准备发送");
						size = tempUrl.size();
						while(size > 0) {
							writer.println(tempUrl.remove(--size));
							writer.flush();  //必须加上强制输出
							send++;
						}
						writer.println("end");
						writer.flush();
						msg = reader.readLine();
						if(msg != null && msg.startsWith("RECEIVEOVER"))
							System.out.println("URL(" + send + "个)发往爬虫:" + entry.getKey() + ip + "成功!");
						else
							System.out.println("URL(" + send + "个)发往爬虫:" + entry.getKey() + ip + "失败!");
						writer.close();
						reader.close();
						socket.close();
						socket = null;
						continue;
					} catch (IOException e) {
						System.out.println("往爬虫" + entry.getKey() + ip + "发送URL失败!");
						e.printStackTrace();
					}
				}// end if
			}// end while
			
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
			synchronized(lock) {
				try {
					lock.wait(); //发完一次后,暂停,等待定时器唤醒
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		} // end for
		
	}
}
