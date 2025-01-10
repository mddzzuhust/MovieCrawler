package crawler.dispatcher;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import crawler.client.CrawlerClient;
import crawler.feeder.Task;

/**
 * 向指定爬虫转发已爬行URL队列
 * 这里只发送在已爬行URL队列中的尚未保存到硬盘中的URL,已保存的URL信息由bitset结构更新
 * @author liuxue
 */
public class URLTransfer extends TimerTask {

	private int crawlerid;
	public URLTransfer(int crawlerid) {
		this.crawlerid = crawlerid;
	}
	
	public void run() {

		if(!CrawlerClient.clients.containsKey(crawlerid) || crawlerid == CrawlerClient.clientID)
			return;
		
		PrintWriter writer = null;
		BufferedReader reader = null;

		List<String> tempUrl = new ArrayList<String>();
		List<?> tempHash = ((ArrayList<?>)CrawlerClient.clients.get(crawlerid));
//		StringBuilder urlcode = new StringBuilder();
//		String url = null;
		
		for(int i = 0; i < Task.urlsCrawled.size(); i++) {
			if(tempHash.contains(i)) {
				tempUrl.addAll(Task.urlsCrawled.get(i));
//				int csize = Task.crawledUrlList.get(i).size();
//				for(int j = 0; j < csize; j++) {
//					url = Task.crawledUrlList.get(i).get(j);
//					urlcode.append(BloomFilter.hash1(url)).append(",");
//					urlcode.append(BloomFilter.hash2(url)).append(",");
//					urlcode.append(BloomFilter.hash3(url)).append(",");
//				}
			}
		}
		
		int size = tempUrl.size();
		if(size > 0) {
			String ip = tempHash.get(0).toString().substring(1);
//			System.out.println("检测爬虫:" + crawlerid + ip);
			try {
				Socket socket = new Socket();
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(30*1000);
				socket.connect(new InetSocketAddress(ip,CrawlerClient.CLIENTPORT));
				System.out.println("准备向爬虫" + socket.getInetAddress() + "转发已爬行队列信息.");
				writer = new PrintWriter(socket.getOutputStream());
				writer.println("URL_MOVE#" + crawlerid);
				writer.flush();
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				System.out.println("发送URL_MOVE完成...");
				String msg = reader.readLine();
//				System.out.println("接收READY完成...");
				if(msg == null || !msg.startsWith("READY")) {
					System.out.println("目标爬虫无法正常接收!");
					writer.close();
					reader.close();
					socket.close();
					socket = null;
					return;
				}
				
				int send = 0;
//				System.out.println("准备发送");
				while(size > 0) {
					writer.println(tempUrl.remove(--size));
					writer.flush();  //必须加上强制输出
					send++;
				}
//				writer.println(url);
				writer.println("end");
				writer.flush();
				msg = reader.readLine();
				if(msg != null && msg.startsWith("RECEIVEOVER"))
					System.out.println("往爬虫转移URL(" + send + "个)" + crawlerid + ip + "成功!");
				else
					System.out.println("往爬虫转移URL(" + send + "个)" + crawlerid + ip + "失败!");
				
				writer.close();
				reader.close();
				socket.close();
				socket = null;
				return;
			} catch (IOException e) {
				System.out.println("往爬虫" + crawlerid + ip + "转移URL失败!");
			}
		}// end if
		tempUrl.clear();
		tempUrl = null;
	}

}
