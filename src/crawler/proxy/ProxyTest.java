package crawler.proxy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 测试代理地址是否有效
 * 有些代理地址可以连接成功,但不能正确返回你要的数据,而仅仅返回代理主机自己的主页
 */
public class ProxyTest {
	
	private volatile int filterThreads = 0;
	private static int CONNECT_TIMEOUT = 50;
	
	private static int POOL_SIZE = 50;
	
	private ExecutorService pool = null; //线程池
	
	private List<Proxy> list = new ArrayList<Proxy>();

	public List<Proxy> test(String file) throws IOException {
		pool = Executors.newFixedThreadPool(POOL_SIZE);
		
		FileReader reader = null;
		Properties prop = new Properties();
		try {
			reader = new FileReader(new File(file));
			prop.load(reader);
		} catch(Exception e) {
			System.out.println("解析代理地址失败:" + e.getMessage());
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(Exception e) {
				}
			}
			reader = null;
		}

		Set<Entry<Object, Object>> set = prop.entrySet();
		for(Entry<Object,Object> obj : set) {
			String ip = obj.getKey().toString().trim();
			int port = Integer.parseInt(obj.getValue().toString().trim());
			pool.execute(new Tester(ip, port));
		}
		waitForTerminate();
		
		List<Proxy> avaliableProxy = new ArrayList<Proxy>();
		String url = "http://www.baidu.com";
		for(Proxy proxy : list) {
			String html = getHtml(url, proxy);
//			System.out.println(html);
			if(html != null && html.indexOf("百度一下，你就知道") != -1) {
				avaliableProxy.add(proxy);
			}
		}
		return avaliableProxy;
	}
	
	private void waitForTerminate() {
		try {
			while(!pool.isTerminated()) {
				Thread.sleep(2000);
				if(filterThreads == 0)
					pool.shutdown();
			}
		} catch (Exception e) {
			pool.shutdownNow();
		}
	}
	
	class Tester implements Runnable {

		private String ip;
		private int port;
		
		public Tester(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}
		@Override
		public void run() {
			filterThreads++;
			Socket socket = null;
			try {
//				System.out.println("开始测试:" + ip + ":" + port);
				socket = new Socket();
				InetSocketAddress inetSocket = new InetSocketAddress(ip, port);
				socket.connect(inetSocket, CONNECT_TIMEOUT);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocket);
				list.add(proxy);
			} catch (Exception e) {
//				System.out.println("代理地址-[" + ip + "]-测试异常:" + e.getMessage());
			} finally {
				try {
					if(socket != null)
						socket.close();
				} catch (IOException e) {
					
				}
			}
			filterThreads--;
		}
	}
	
	public String getHtml(String url, Proxy proxy) {
		java.net.URL turl = null;
		HttpURLConnection conn = null;
		try {
			turl = new java.net.URL(url);
			conn = (HttpURLConnection) turl.openConnection(proxy);
			conn.setConnectTimeout(30000);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		StringBuilder html = new StringBuilder(15000);
    	BufferedInputStream in = null;
    	byte[] temp = new byte[2048];
    	String charset = "gbk";
    	try {
        	int responseCode = conn.getResponseCode();
        	if(responseCode != HttpURLConnection.HTTP_OK)
        		return null;
            in = new BufferedInputStream(conn.getInputStream());
            String inputLine;
            int bytesRead = 0;
            int totalBytes = 0;
            while ((bytesRead = in.read(temp)) >= 0) {
            	totalBytes += bytesRead;
            	if(totalBytes > 5242880) {
            		System.out.println("内容过大,丢弃");
            		return null;
            	}
                inputLine = new String(temp, 0, bytesRead, charset);
                html.append(inputLine);
            }
            return html.toString();
    	} catch(Exception e) {
    		e.printStackTrace();
    		System.err.println("error proxy:" + proxy.address());
    	} finally {
    		try {
				if(in != null)
					in.close();
			} catch (IOException e) {
			}
			conn.disconnect();
    	} 
    	return html.toString();
    }
	
	public static void main(String[] args) {
		String file = "proxy.properties";
		java.util.List<Proxy> list = null;
		try {
			list = new ProxyTest().test(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(list.size() == 0)
			System.out.println("empty");
		for(Proxy proxy : list) {
			System.out.println(proxy.address().toString().substring(1));
		}
	}
}
