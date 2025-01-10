package crawler.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Date;
import java.util.Set;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 改进方法.对发现的代理地址不只是简单的根据响应时间的阈值进行过滤
 * 检测之后对可用地址按响应时间从小到大进行排序.这样可以更好的选择
 * @author liuxue
 */
public class ProxyDiscover3 {

	/*
	 * 我使用parseThreads的值来标识正在解析网页内容的线程数.
	 * 当它为0时,表示解析完毕,正在对代理地址进行过滤,并且所有任务均已添加到线程池中
	 * 这时我可以调用shutdown()来关闭线程池.但这似乎仍有一个问题,因为parseThreads
	 * 的初始值为0,可能在所有的解析线程尚未启动前,就被执行了shutdown(),导致程序无法
	 * 按预期行为工作.但这个可能几乎不会发生,所以我仍然这样使用.我仍在寻求更好的方法
	 * 注:可以在往线程池加入线程前使parseThreads的值加1,在线程结束时将其减1.但我觉
	 * 得这不是一个理想的方法,因为这会使得程序难于理解,并且我觉得有潜在的隐患.
	 */
	private volatile int parseThreads = 0;
	private volatile int filterThreads = 0;
	
	private Hashtable<String, String> coarseProxy;
	private Hashtable<String, String> usefulProxy;
	
	private ExecutorService pool = null; //线程池
	
	public static int POOL_SIZE = 30; //线程池的容量
	
	public static int CONNECT_TIMEOUT = 50; //代理响应超时时间
	
	private static Pattern scriptPattern = Pattern.compile("<SCRIPT type=\"text/javascript\">[\n\r]?(([a-z]=\"[0-9]\";)+)[\n\r]?</SCRIPT>", Pattern.DOTALL);
	private static Pattern addressPattern = Pattern.compile("<tr><td>([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})<SCRIPT type=text/javascript>document.write\\(\":\"((\\+[a-z])+)\\)</SCRIPT></td><td>HTTP</td><td>(.+?)</td>(<td>([^/<>]+?)</td>)?</tr>", Pattern.DOTALL);
	
	public ProxyDiscover3() {
		coarseProxy = new Hashtable<String, String>();
		usefulProxy = new Hashtable<String, String>();
	}
	
	/**
	 * 寻找代理地址.其实就是解析指定站点公布的代理地址
	 * @param proxyfile 要解析的站点URL
	 * @return 解析是否成功
	 */
	public boolean parse(String proxyfile) {
		//ProxyPool.parse("proxy.properties");
		File file = null;
		BufferedReader reader = null;
		try {
			file = new File(proxyfile);
			if(!file.exists() || file.isDirectory()) {
				return false;
			}
			
			String url;
			pool = Executors.newFixedThreadPool(POOL_SIZE);
			reader = new BufferedReader(new FileReader(file));
			while((url = reader.readLine()) != null) {
				if(url.startsWith("http"))
					pool.execute(new Parser(url));
			}
		} catch(Exception e) {
			System.out.println("获取地址信息出错");
			return false;
		} finally {
			if(reader != null)
			try {
				reader.close();
			} catch (IOException e1) {
			}
			waitForTerminate();
		}
		//proxySave(coarseProxy, "find-proxy.txt");
		System.out.println("共发现代理地址:" + coarseProxy.size());
		String[] proxyList = sortProxy(usefulProxy);
		//proxySave(usefulProxy, "useful-proxy.txt");
		//proxySave(proxyList, "sort-proxy.txt");
		int total = avaliable(proxyList);
		System.out.println("过滤完毕,可用代理地址数:" + total);
		return total != 0;
	}
	
	private int avaliable(String[] proxyList) {
		String url = "http://feeds.delicious.com/v2/rss/network/JuanjoGarcia";
		String separator = System.getProperty("line.separator");
		FileWriter writer = null;
		try {
//			writer = new FileWriter("avaliableProxy.properties");
			writer = new FileWriter("avaliableProxy.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
			return 0;
		}
		int total = 0;
		String comment = "";
		InetSocketAddress inetSocket = null;
		for(String address : proxyList) {
			int pos = address.indexOf('#');
			if(pos != -1) {
				comment = address.substring(pos);
				address = address.substring(0, pos);
				System.err.println(comment);
			}
			pos = address.indexOf(':');
			String ip = address.substring(0, pos).trim();
			int port = Integer.parseInt(address.substring(pos + 1).trim());
			inetSocket = new InetSocketAddress(ip, port);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocket);
			try {
				java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new URL(url)
						.openConnection(proxy);
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);
				int responseCode = conn.getResponseCode();
				String contentType = conn.getContentType();
				String msg = proxy + "\t" + "contentType:" + contentType
						+ "\tresponseCode:" + responseCode;
				System.out.println(msg);
				conn.disconnect();
				if(responseCode == 200 && contentType.indexOf("application/rss+xml") != -1) {
					String p = proxy.toString();
					writer.write(comment + separator);
					writer.write(p.substring(p.indexOf('/') + 1) + separator);
					writer.flush();
					total++;
				}
			} catch (Exception e) {
				System.err.print(proxy);
				System.err.println(e.getMessage());
			}
		}
		if(writer != null)
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return total;
	}
	
	private String[] sortProxy(Hashtable<String, String> proxy) {
		if(proxy.size() == 0)
			return null;
//		System.out.println(proxy.keySet().toString());
		String keySetString = proxy.keySet().toString();
		String[] proxyList = keySetString.substring(1, keySetString.length() - 1).split(", ");
		for(int i = 0; i < proxyList.length; i++)
			System.out.println(proxyList[i]);
		String minValue = null;
		String value = null;
		int minIndex = 0;
		for(int i = 0; i < proxyList.length; i++) {
			minValue = proxy.get(proxyList[i]);
			minIndex = i;
			for(int j = i + 1; j < proxyList.length; j++) {
				value = proxy.get(proxyList[j]);
				if(value.compareTo(minValue) < 0) {
					minValue = value;
					minIndex = j;
				}
			}
			if(minIndex != i) {
				minValue = proxyList[i];
				proxyList[i] = proxyList[minIndex] + ", time=" + proxy.get(proxyList[minIndex]);
				proxyList[minIndex] = minValue;
			} else {
				proxyList[i] = proxyList[i] + ", time=" + proxy.get(proxyList[i]) + "ms";
			}
			
		}
		return proxyList;
	}

	private void waitForTerminate() {
		try {
			while(!pool.isTerminated()) {
				Thread.sleep(2000);
				if(parseThreads == 0 && filterThreads == 0)
					pool.shutdown();
			}
		} catch (Exception e) {
			pool.shutdownNow();
		}
	}

	/**
	 * 从网页内容中提取代理地址信息.使用正则表达式提取
	 * 分两步,第一步提取端口表示信息,第二步提取地址和描述信息
	 * @param html 包含代理地址信息的网页内容
	 * @return 提取到的地址信息 key=IP:PORT, value=描述信息
	 */
	private synchronized Hashtable<String, String> getProxyAddress(String html) {
		Hashtable<Character, Character> portMap = new Hashtable<Character, Character>();
		Matcher matcher = scriptPattern.matcher(html);
		while(matcher.find()) {
//			System.out.println("find:" + matcher.group(1));
			String[] find = matcher.group(1).replaceAll("\"", "").split(";");
			for(int i = 0; i < find.length; i++)
				portMap.put(find[i].charAt(0), find[i].charAt(2));
		}
		if(portMap.isEmpty())
			return null;
		
		Hashtable<String, String> address = new Hashtable<String, String>();
		matcher = addressPattern.matcher(html);
		while(matcher.find()) {
//			System.out.println("find:" + matcher.group(1) + ":" 
//					+ matcher.group(2) + " @ " + matcher.group(6));
			String newport = ":";
			char[] oldport = matcher.group(2).replaceAll("\\+", "").toCharArray();
			for(int i = 0; i < oldport.length; i++) {
				if(!portMap.containsKey(oldport[i]))
					return null;
				newport += portMap.get(oldport[i]);
			}
			String name = matcher.group(6);
			if(name == null)
				name = "unknown";
			address.put(matcher.group(1) + newport, name);
		}
//		System.out.println("共有地址:" + address.size());
//		proxyPrintToScreen(address);
		return address;
	}
	
	@SuppressWarnings("unused")
	private void proxyPrintToScreen(Hashtable<String, String> address) {
		Set<String> set = address.keySet();
		for(String addr : set)
			System.out.println(addr + "  #" + address.get(addr));
	}

	class Parser implements Runnable {

		String url;
		public Parser(String url) {
			this.url = url;
		}
		
		@Override
		public void run() {
			parseThreads++;
			System.out.println("开始解析:" + url);
			Hashtable<String, String> address = null;
			try {
				String html = ProxyHelper.urlToHTML(url);
				address = getProxyAddress(html);
			} catch (Exception e) {
				System.out.println("解析地址出错:" + url);
				e.printStackTrace();
				parseThreads--;
				return;
			}
			
			if(address == null || address.isEmpty()) {
				parseThreads--;
				return;
			}
			Set<String> set = address.keySet();
			for(String addr : set) { //注意Hashtable本身实现了同步
				coarseProxy.put(addr, address.get(addr));
				pool.execute(new Tester(addr));
			}
			parseThreads--;
		}
		
	}
	
	/**
	 * 线程类.判断代理地址的响应速度,
	 * 连接时间大于{@link ProxyDiscover.CONNECT_TIMEOUT}的代理地址将被丢弃
	 * @author Administrator
	 */
	class Tester implements Runnable {

		private String addr; //待测试的地址,形如"ip:port"
		
		public Tester(String addr) {
			this.addr = addr;
		}
		@Override
		public void run() {
			filterThreads++;
			Socket socket = null;
			String ip = addr.substring(0, addr.lastIndexOf(':'));
			int port = Integer.parseInt(addr.substring(addr.indexOf(':') + 1));
			try {
				socket = new Socket();
				SocketAddress socketAddress = new InetSocketAddress(ip, port);
				long before = System.currentTimeMillis();
				socket.connect(socketAddress, CONNECT_TIMEOUT);
				long after = System.currentTimeMillis();
				String description = coarseProxy.get(addr);
				usefulProxy.put(addr + "  #" + description, (after-before) + "");
			} catch (Exception e) {
				System.out.println("代理地址-[" + addr + "]-测试异常:" + e.getMessage());
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					
				}
			}
			filterThreads--;
		}
		
	}
	
	public void proxySave(Hashtable<String, String> proxy) {
		proxySave(proxy, "cnproxy.txt");
	}
	
	/**
	 * 将可用代理地址信息写入文件中
	 * 默认写入文件名为"cnproxy.txt"
	 */
	public void proxySave(Hashtable<String, String> proxy, String filename) {
		if(proxy == null || proxy.isEmpty())
			return;
		if(filename == null || filename.equals(""))
			filename = "cnproxy.txt";
		String lineSeparator = System.getProperty("line.separator");
		FileWriter writer = null;
		try {
			writer = new FileWriter(filename);
			final Set<String> set = proxy.keySet();
			for(String addr : set) {
				String description = proxy.get(addr);
				writer.write(addr + "  #" + description + lineSeparator);
			}
			writer.write("# total:" + proxy.size() + 
					", time:" + new Date(System.currentTimeMillis()));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null)
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}
	
	public void proxySave(String[] proxy) {
		proxySave(proxy, "cnproxy.txt");
	}
	
	/**
	 * 将可用代理地址信息写入文件中
	 * 默认写入文件名为"cnproxy.txt"
	 */
	public void proxySave(String[] proxy, String filename) {
		if(proxy == null || proxy.length == 0)
			return;
		if(filename == null || filename.equals(""))
			filename = "cnproxy.txt";
		String lineSeparator = System.getProperty("line.separator");
		FileWriter writer = null;
		try {
			writer = new FileWriter(filename);
			for(int i = 0; i < proxy.length; i++) {
				writer.write(proxy[i] + lineSeparator);
			}
			writer.write("# total:" + proxy.length + 
					", time:" + new Date(System.currentTimeMillis()));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null)
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		ProxyDiscover3 finder = new ProxyDiscover3();
//		URLSummary summary = new URLSummary();
//		String url = "http://www.cnproxy.com/proxy8.html";
//		String html = summary.getHtml(new URL(url), null);
//		finder.getProxyAddress(html);
		long a = System.currentTimeMillis();
		finder.parse("proxySourcePage.txt");
		long b = System.currentTimeMillis();
		System.out.println("耗时:" + (b-a));
	}
}
