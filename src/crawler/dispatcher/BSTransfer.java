package crawler.dispatcher;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import crawler.client.CrawlerClient;

/**
 * <b>这个类也没有再用了</b>
 * 将自己的bitset判重信息发送给指定主机
 * @author Administrator
 *
 */
public class BSTransfer implements Runnable {
	
	String address;
	
	public BSTransfer(String ip) { //ip的形式为: "/127.0.0.1"
		this.address = ip.substring(1);
	}
	
	public void run() {
		Socket socket = new Socket();
		PrintWriter writer = null;
		ObjectOutputStream oos = null;
		try {
			
			socket.setTcpNoDelay(true);
	        socket.setSoTimeout(30*1000);
	        socket.connect(new InetSocketAddress(address,CrawlerClient.CLIENTPORT));
			writer = new PrintWriter(socket.getOutputStream());
			writer.println("BITSETSTRANSFER");
			writer.flush();
			
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(CrawlerClient.bfilter.getBits());
			oos.flush();
		} catch (UnknownHostException e) {
			System.out.println("转移BITSET结构发生错误");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("转移BITSET结构发生错误");
			e.printStackTrace();
		} finally {
			writer.close();
			try {
				oos.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
