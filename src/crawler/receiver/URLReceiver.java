package crawler.receiver;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import crawler.client.CrawlerClient;
import crawler.feeder.Task;

public class URLReceiver implements Runnable {

	Socket socket;
	BufferedReader reader;
	PrintWriter writer;
	int type; //1:表示接收新任务   2:表示接收已爬行队列(在对方爬虫准备退出的时候)
	
	public URLReceiver(Socket socket,int type) {
		this.socket = socket;
		this.type = type;
	}
	
	public void run() {
		
		try {
			socket.setTcpNoDelay(true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			writer.println("READY");
			writer.flush();
			
			int sum = 0;
//			System.out.println("准备接收");
			String url = reader.readLine();
			while(url != null && !url.startsWith("end")) {
				if(type == 1) {
					Task.urlsToTest.add(url);
				} else if(type == 2) {
//					String[] urls = url.split(",");
//					for(int i = 0; i < urls.length; i++) {
//						CrawlerClient.bfilter.setPosition(Integer.parseInt(urls[i]));
//					}
					CrawlerClient.bfilter.add(url);
				}
				sum++;
				url = reader.readLine();
			}
			writer.println("RECEIVEOVER");
			writer.flush();
			if(type == 1)
				System.out.println("接收爬虫" + socket.getInetAddress() + "的任务URL(" + sum + ")消息");
			if(type == 2)
				System.out.println("接收爬虫" + socket.getInetAddress() + "的转移URL(" + sum + ")消息");
		} catch (IOException e) {
			if(type == 1)
				System.out.println("接收爬虫" + socket.getInetAddress() + "的任务URL消息失败");
			if(type == 2)
				System.out.println("接收爬虫" + socket.getInetAddress() + "的转移URL消息失败");
		} finally {
			try {
				reader.close();
				writer.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
