package crawler.receiver;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.BitSet;

import crawler.client.CrawlerClient;

/**
 * <b>这个类可能永远不会使用了...嘿嘿</b>
 * 接收其他爬虫发来的BloomFilter中的BitSet,用以判重.
 * 每个BitSet流数据为32MB,有些大,尽量少用
 * @author liuxue
 */
public class BSReceiver implements Runnable {

	Socket socket;
	
	public BSReceiver(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			BitSet bits = (BitSet) ois.readObject();
			synchronized(CrawlerClient.bfilter) {
				CrawlerClient.bfilter.addBits(bits);
			}
		} catch (IOException e) {
			System.out.println("获取BITSET信息失败!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("获取BITSET信息失败!");
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
