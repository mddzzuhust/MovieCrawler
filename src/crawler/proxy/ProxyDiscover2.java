package crawler.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Set;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 只实例化一个线程池,线程解析完页面后马上检测发现的代理地址的可用性
 * 解析页面和检测地址,这两者并行处理,线程池不用等待,速度应该会有提升
 * @author liuxue
 */
public class ProxyDiscover2 {

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
	
	public static int POOL_SIZE = 10; //线程池的容量
	
	public static int CONNECT_TIMEOUT = 50; //代理响应超时时间
	
	private static Pattern scriptPattern = Pattern.compile("<SCRIPT type=\"text/javascript\">[\n\r]?(([a-z]=\"[0-9]\";)+)[\n\r]?</SCRIPT>", Pattern.DOTALL);
	private static Pattern addressPattern = Pattern.compile("<tr><td>([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})<SCRIPT type=text/javascript>document.write\\(\":\"((\\+[a-z])+)\\)</SCRIPT></td><td>HTTP</td><td>(.+?)</td>(<td>([^/<>]+?)</td>)?</tr>", Pattern.DOTALL);
	
	public ProxyDiscover2() {
		coarseProxy = new Hashtable<String, String>();
		usefulProxy = new Hashtable<String, String>();
	}
	
	/**
	 * 寻找代理地址.其实就是解析指定站点公布的代理地址
	 * @param proxyfile 要解析的站点URL
	 * @return 解析是否成功
	 */
	public boolean parse(String proxyfile) {
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
		//proxySave(coarseProxy, "oldproxy.txt");
		System.out.println("共发现代理地址:" + coarseProxy.size());
		//proxySave(usefulProxy, "cnproxy.txt"); 
		//proxySave(usefulProxy, "proxy.properties");
		System.out.println("过滤完毕,可用代理地址数:" + usefulProxy.size());
		return !usefulProxy.isEmpty();
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
				socket.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT);
				String description = coarseProxy.get(addr);
				usefulProxy.put(addr, description);
			} catch (Exception e) {
				//System.out.println("代理地址-[" + addr + "]-测试异常:" + e.getMessage());
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
		if(proxy.isEmpty())
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
	
	public static void main(String[] args) throws Exception {
		ProxyDiscover2 finder = new ProxyDiscover2();
		long a = System.currentTimeMillis();
		finder.parse("proxySourcePage.txt");
		long b = System.currentTimeMillis();
		System.out.println("耗时:" + (b-a));
	}
}
