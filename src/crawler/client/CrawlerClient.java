package crawler.client;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.net.DatagramPacket;
//import java.net.InetAddress;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetSocketAddress;
//import java.net.MalformedURLException;
//import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import crawler.BloomFilter;
import crawler.RmiInterface;
import crawler.config.Config;
//import crawler.config.Config;
import crawler.crawler.Crawler;
import crawler.crawler.CrawlerThreadExceptionHandler;
import crawler.dispatcher.BSTransfer;
import crawler.dispatcher.URLTransfer;
import crawler.feeder.FeedThread;
import crawler.feeder.Task;
//import crawler.io.FileURLProvider;
//import crawler.io.IURLProvider;
import crawler.receiver.BSReceiver;
import crawler.receiver.URLReceiver;
import crawler.sql.Pool;

public class CrawlerClient implements Runnable {
	
	public static int clientID;                  //本爬虫ID
	public static HashMap<?, ?> clients;         //保存爬虫ID与IP地址及所拥有的哈希号的信息,元素为数组类型,
	                                             //数组第0项保存其IP地址,其后保存爬虫对应的哈希号.ID值为key
	public static ArrayList<?> hashCode;         //本爬虫哈希号,注意与crawler中保持同步
	public static BloomFilter bfilter;           //用于判断URL是否已处理过
	public static int totalHashNum = 24;          //设定总的hash数
	public ScheduledExecutorService schedule;    //定时器,定时执行URL保存功能
	
	/* 
	 * 将它定义为static,这应该算是设计上的失误吧.
	 * 我在Task中有一个静态方法需要调用RMI,只好... 
	 */
	public static RmiInterface master;                  //准备RMI调用服务器方法
	public static Thread feedThread;                    //主线程(判重线程)
	public static HashMap<String,Thread> threadMap;     //线程哈希队列
	public ServerSocket serversocket;                   //监听ServerSocket
	public Crawler crawler = null;                      //爬行下载实例(Runnable)
	
	public static final int SERVERPORT = 6000;          //服务端监听端口
	public static final int CLIENTPORT = 9876;          //爬虫监听端口
	public static String HOSTADDRESS = "";              //服务器地址
	public static int NUM_OF_THREAD = 5;                //启动爬行线程个数(现在从配置文件中读取)
	public static volatile boolean running = false;     //运行状态标志
	public static volatile boolean listen = false;      //爬虫监听标志
	
	public static final boolean debug = false;
	
	public CrawlerClient() {
		
	}
	
	public boolean initClient(RmiInterface master, String filename,
			String hostaddress, int threads, boolean increment) {
		CrawlerClient.clientID = 0;
		CrawlerClient.clients = null;
		CrawlerClient.bfilter = new BloomFilter(increment);

		CrawlerClient.HOSTADDRESS = hostaddress;
		CrawlerClient.NUM_OF_THREAD = threads;

		CrawlerClient.threadMap = new HashMap<String, Thread>(
				CrawlerClient.NUM_OF_THREAD);
		CrawlerClient.master = master;
		try {
			// 增量爬行，读取上次缓存在本地磁盘上的链接
			if(increment){
				System.out.println("增量爬行.读取上次缓存的链接.");
				/**
				 * 本方法会导致FileURLProvider中的toReadUrlFileIndex不一致
				 * 下面的方法调用后，FileURLProvider中的toReadUrlFileIndex会加1（读取会删除该文件）
				 * 而Task.urlProvider中的toReadUrlFileIndex初始化时仍会直接读取status.properties文件
				IURLProvider urlProvider = new FileURLProvider();
				urlProvider.loadToCrawlUrls();*/
				Task.urlProvider.loadToCrawlUrls();
			}
			
			readInitURL(filename);
		} catch (IOException e) { // 读入初始URL列表失败
			Client.showMessage("没有指定初始URL列表文件");
			System.out.println("没有指定初始URL列表文件");
		}

		Client.showMessage("爬虫客户端初始化完成.");
		System.out.println("爬虫客户端初始化完成.");
		return true;
	}
	
	// 读取URL初始文件,默认为initURL.txt文件
	public boolean readInitURL(String filename) throws IOException {
		File file = new File(filename);
		if(!file.exists()) {
			Client.showMessage("找不到初始URL文件.");
			System.out.println("找不到初始URL文件.");
			return false;
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String url = null;
		while((url=reader.readLine()) != null) {
			if(url.startsWith("http") && !Task.initialUrls.contains(url)){
				//System.out.println("初始化URL(客户端文件)：" + url);
			//	url = "9" + url;
				Task.initialUrls.add(url);
			}
		}
		
		reader.close();
		return true;
	}

	public boolean discoverServer() {
		String msg = "";
		BufferedReader reader = null;
		PrintWriter writer = null;
		Socket socket = null;
		boolean flag;
		try {
//			OK,简单起见,暂时不实现多播寻找服务器.
//			MulticastSocket ssocket = null;
//			InetAddress address = InetAddress.getByName("255.255.255.255");
//			DatagramPacket packet = new DatagramPacket("CRAWLERFIND".getBytes(), 12 , address, CrawlerClient.SERVERPORT);
//			ssocket.joinGroup(address);
//			ssocket.send(packet);
			
			socket = new Socket(HOSTADDRESS,SERVERPORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			writer.println("CRAWLERFIND");
			writer.flush();  //必须加上强制输出
			msg = reader.readLine();
			//System.out.println("进程通信内容：" + msg);
			if(msg.startsWith("accept=")) {
				clientID = Integer.parseInt(msg.substring(msg.indexOf("=")+1));
				new Thread(this).start();  //开始监听
				getCrawledURL();  //获取已爬行URL信息
				int times = 0;
				while(!CrawlerClient.listen && times < 5) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						
					}
					times++;
				}
				if(CrawlerClient.listen) {
					writer.println("INITDONE");
					writer.flush();
					String response = reader.readLine();
					if(response != null && response.equals("done"))
						flag = true;
					else
						flag = false;
				} else {
					writer.println("INITFAILED");
					writer.flush();
					flag = false;
				}
			} else if(msg.equals("deny")) {
				Client.showMessage("服务器拒绝连接.");
				System.out.println("服务器拒绝连接.");
				flag = false;
			} else {
				Client.showMessage("服务器没有回应.");
				System.out.println("服务器没有回应.");
				flag = false;
			}
			//这些异常是怎么找到的
		} catch (UnknownHostException e) {
			Client.showMessage("未发现爬虫服务器.");
			System.out.println("未发现爬虫服务器.");
//			e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			Client.showMessage("网络错误.");
			System.out.println("网络错误.");
//			e.printStackTrace();
			flag = false;
		} finally {
			try {
				if(writer != null)
					writer.close();
				if(socket != null)
					socket.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		if(flag) {
			Client.showMessage("连接服务器" + socket.getInetAddress() + "成功!获得爬虫ID号为:" + clientID);
			System.out.println("连接服务器" + socket.getInetAddress() + "成功!获得爬虫ID号为:" + clientID);
		}
		return flag;
	}
	
	public boolean getInitInformation() {
		try {
			CrawlerClient.totalHashNum = master.getTotalHashNum();
			Task.init(CrawlerClient.totalHashNum); //好吧,在这里调用它是有点奇怪.但不要改变它,否则程序出错
			if(!getClients()) {
				System.err.println("无法从服务端获取爬虫信息.程序不能启动.");
				return false;
			}
			if(!getInitURL()) {
				System.out.println("获取服务端初始URL失败.爬虫只使用本身初始URL进行爬行.");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean getClients() throws RemoteException {
		//没有实现怎么调用
		clients = master.getClients();
		if(clients == null || clients.size() == 0)
			return false;
		
		CrawlerClient.hashCode = (ArrayList<?>)clients.get(CrawlerClient.clientID);
		System.out.println("获得哈希号:" + CrawlerClient.hashCode);
		return true;
	}

	public boolean getInitURL() { //获取服务端指定的起始URL
		List<String> serverURL;
		try {
			serverURL = master.getInitURL(CrawlerClient.hashCode);
			if(serverURL == null) return false;
		} catch (RemoteException e) {
			return false;
		}
		
		String url = "";
		for(int i = 0; i < serverURL.size(); i++) {
			url = serverURL.get(i);
			if(url.startsWith("http") && !Task.initialUrls.contains(url)){
				System.out.println("初始化URL(服务端)："+url);
				Task.initialUrls.add(serverURL.get(i));
			}
		}
		serverURL.clear();
		serverURL = null;
		return true;
	}	
	
	public void schedule() {
		Client.showMessage("[" + Task.getTime() + "] - 启动线程开始爬行...");
		System.out.println("[" + Task.getTime() + "] - 启动线程开始爬行...");
		
		System.out.println("起始URL:-[ " + Task.initialUrls.size() + " ]-个");
		//System.out.println("起始URL:-[ " + Task.initialUrls);
		Task.urlsToTest.addAll(Task.initialUrls);
		feedThread = new Thread(new FeedThread(), "thread#" + 0);
		feedThread.setPriority(Thread.NORM_PRIORITY + (Thread.MAX_PRIORITY-Thread.NORM_PRIORITY)/2);
		if(CrawlerClient.clients.size() == 1) {
			System.out.println("即时启动判重线程");
			CrawlerClient.running = true;
			feedThread.start();
		} else { //延时启动,尽力先等待其他爬虫将已爬行任务信息发送过来
			System.out.println("延时启动判重线程");
			new Timer().schedule(new java.util.TimerTask(){
				public void run() {
					CrawlerClient.running = true;
					feedThread.start();
				}
			}, 60000);
		}
		
		crawler = new Crawler();
		UncaughtExceptionHandler uhexception = new CrawlerThreadExceptionHandler(crawler);
		for(int i = 1; i <= CrawlerClient.NUM_OF_THREAD; i++) {
			Thread thread = new Thread(crawler, "" + i);
			thread.setUncaughtExceptionHandler(uhexception);
			threadMap.put(thread.getName(), thread);
			thread.start();
		}
		System.out.println("启动线程" + threadMap.size() + "个");
		
		int scheduleTime = Config.getInstance().getScheduleTime(); //调度间隔时间
		schedule = Executors.newScheduledThreadPool(1);
		schedule.scheduleAtFixedRate(new Task(), scheduleTime, scheduleTime, TimeUnit.SECONDS); 
	}
	//run在哪里调用？
	public void run() {
		listen();
	}
	
//	@SuppressWarnings(value={"deprecation"})
	public void listen() {
		BufferedReader reader = null;
		PrintWriter writer = null;
		String msg = "";
		try {
			serversocket = new ServerSocket(CrawlerClient.CLIENTPORT);
		} catch (IOException e) {
			Client.showMessage("启动监听失败!");
			System.out.println("启动监听失败!");
			e.printStackTrace();
			return;
		}
		
		CrawlerClient.listen = true;
		while(CrawlerClient.listen) { //爬虫进入监听状态,监听端口:4000
			try {
				Socket socket = serversocket.accept();
//				socket.setReceiveBufferSize(1024*1024);
//				socket.setSendBufferSize(1024);
		        socket.setTcpNoDelay(true);
		        socket.setSoTimeout(30*1000); //半分钟内必须处理完
		        
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				msg = reader.readLine();
				System.out.println("收到消息:" + msg + ",来自" + socket.getInetAddress() + ":" + socket.getPort());
				//这些字符串哪里来的？
				if(msg.startsWith("CRAWLER_JOINED")) {
					int crawlerid = Integer.parseInt(msg.substring(msg.indexOf("#")+1));
					if(msg.startsWith("CRAWLER_JOINED") && crawlerid == CrawlerClient.clientID && clients != null)
						continue;  //这是自己加入系统时服务端发出的广播信息.其实自己是不会收到自己加入时的广播消息的
					
					if(getClients()) {
//						Timer sender = new Timer(); //本是为避免多个爬虫同时往新爬虫发送数据造成负担,现在我要让它们尽快完成,所以不延时
//						sender.schedule(new URLTransfer(crawlerid), (long) (new java.util.Random().nextInt(5) * 10000) + 10000);
						new Thread(new URLTransfer(crawlerid)).start();
						System.out.println("有新爬虫加入.爬虫信息更新成功!");
					} else {
						Client.showMessage("爬虫更新,但获取爬虫信息失败!");
						System.out.println("爬虫更新,但获取爬虫信息失败!");
					}
					reader.close();
					socket.close();
					continue;
				} else if(msg.startsWith("CRAWLER_LEFT")) { //不管正常还是异常,都要重新获取BitSet结构
					getClients();
					reader.close();
					socket.close();
					CrawlerClient.bfilter.addBits(master.getCrawledURL());
					System.out.println("获取BitSet结构成功");
					continue;
				}
//				else if(msg.startsWith("UPDATE_URLLIST")) { //没人让你上传,自己上传,在已爬行队列长度足够的时候
//					updateCrawledURL();
//					reader.close();
//					socket.close();
//					continue;
//				}
				else if(msg.startsWith("URL_RECEIVE")) {
					new Thread(new URLReceiver(socket,1)).start();
					continue;
				} else if(msg.startsWith("URL_MOVE")) {
					new Thread(new URLReceiver(socket,2)).start();
					continue;
				} else if(msg.startsWith("BITSETSTRANSFER")) {
					new Thread(new BSReceiver(socket)).start();
					continue;
				} else if(msg.startsWith("CRAWLER_STATUS")) {
					// 服务端定时消息,检查爬虫状态
					writer = new PrintWriter(socket.getOutputStream());
					writer.println("NORMAL[" + (Task.getURLProvider().getSavedUrlsCrawledSize() + Task.urlsToCrawl.size()) + "]");
					writer.flush();
					writer.close();
					reader.close();
					socket.close();
					System.gc();
					continue;
				} else if(msg.startsWith("UPDATE")) {
					getClients();
					String update = msg.substring(msg.indexOf("["), msg.length() - 1);
					int from = Integer.parseInt(update.substring(1, update.indexOf(',')));
					if(from == CrawlerClient.clientID) {
						int to   = Integer.parseInt(update.substring(update.indexOf(',') + 1));
						String ip = ((ArrayList<?>)(CrawlerClient.clients.get(to))).get(0).toString();
						new Thread(new BSTransfer(ip)).start();
					}
					reader.close();
					socket.close();
					continue;
				} else if(msg.startsWith("SERVER_EXIT")) {
					reader.close();
					socket.close();
					stopCrawler();
					Client.showMessage("服务端指令结束爬行,爬虫退出.");
					System.out.println("服务端指令结束爬行,爬虫退出.");
					break;
				}
			} catch (Exception e) {
				System.out.println("退出监听!");
				continue;
			}
		}
		Client.showMessage("爬虫已停止监听.");
		System.out.println("爬虫已停止监听.");
		
		if(serversocket != null) {
			try {
				serversocket.close();
			} catch (IOException e) {
			}
		}
	}

	public void getCrawledURL() throws RemoteException {
		BitSet bs = master.getCrawledURL();
		CrawlerClient.bfilter.addBits(bs);
	}
	
	public static void updateCrawledURL(int crawls, int errors) throws RemoteException {
		master.updateCrawledURL(Task.updateSet,                //上传的类型BITSET结构
								crawls,                        //已爬行的URL个数
								errors,                        //发生错误的URL个数
								CrawlerClient.clientID);       //爬虫ID
	}
	
	public void stopCrawler() {
		listen = false; //结束监听线程
		running = false; //结束主线程及URL分发线程
		crawler.stopCrawl(); //结束爬行线程
	
		Socket socket = null;
		PrintWriter writer = null;
		BufferedReader reader = null;
		try {
			Task.getURLProvider().saveErrorUrls();
			Task.getURLProvider().saveCrawledUrls(); //该方法会同步调用上传URL至服务端
			
			socket = new Socket();
			socket.setTcpNoDelay(true);
			socket.connect(new InetSocketAddress(HOSTADDRESS,SERVERPORT), 15000);
			socket.setSoTimeout(30000); //等待30秒,若服务器在30内无响应,则强行退出.
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer.println("CRAWLEREXIT#" + clientID);
			writer.flush();
			Client.showMessage("正在向服务器提交请求");
			System.out.println("正在向服务器提交请求");
			String returnMsg = reader.readLine();
			if(returnMsg != null && returnMsg.startsWith("exit_accept")) {
				System.out.println("收到服务器回应:同意退出.");
			} else {
				System.out.println("服务器异常,准备强行退出.");
			}
		} catch(RemoteException e) {
			System.out.println("上传已爬行队列失败,退出失败.");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.out.println("无法连接服务器,退出失败.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("退出时发生网络错误.");
			e.printStackTrace();
		} finally {
			try {
				if(writer != null)					writer.close();
				if(reader != null)					reader.close();
				if(socket != null)					socket.close();
			} catch (IOException e) {	}
		}
		
		if(serversocket != null) { //这里再写一个,可能没用.但因为端口一直监听没有释放,只好补上了.
			try {
				serversocket.close();
			} catch (IOException e) {
			}
		}
		
		try {
			getClients();
		} catch (RemoteException e) {
			System.out.println("获取新的爬虫信息失败,会导致部分URL没有爬虫接收而丢失.");
		}
		long begin = System.currentTimeMillis(), end;
		while(threadMap.size() > 0) {
			end = System.currentTimeMillis();
			if(end - begin > 1000 * 180)
				break;
			stopThread();
		}
		if(threadMap.size() > 0) {
			System.out.println("剩余线程-[ " + threadMap.size() + " ]-个无法结束.");
			// 临时打印线程状态,为何不能正常结束?
			Iterator<?> it = threadMap.entrySet().iterator();
			java.util.Map.Entry<?, ?> entry = null;
			while(it.hasNext()){
				entry = (java.util.Map.Entry<?, ?>)it.next();
				Thread eThread = (Thread)entry.getValue();
				System.err.println("线程-[ " + eThread.getName() + 
						" ]-退出失败, 当前状态: " + eThread.getState() + 
						" 是否中断: " + eThread.isInterrupted());
			}
		}
		
		//保存URL信息.待判重列表中的URL不再处理,任其丢失.(数量较小影响不大)
		Task.getURLProvider().saveToCrawlUrls();
		Task.getURLProvider().saveToDispatchUrls();
		//if(Config.getInstance().isUseBDB()) // 判断是否用到BDB 放到 Task.closeBDB()中
			Task.closeBDB(); // 关闭 Berkeley DB数据库
		
		schedule.shutdown();
		try {
			schedule.awaitTermination(100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		} finally {
			schedule.shutdownNow();
		}
		
		if (Config.getInstance().isUseDatabase())
			Pool.getInstance().release();
		Task.getURLProvider().saveUrlStatus();
		CrawlerClient.threadMap.clear();
		Client.showMessage("爬虫退出.");
		System.out.println("爬虫退出.");
	}
	
	/**
	 * 停止线程执行
	 * @throws InterruptedException 
	 */
	private void stopThread() {
		Thread exitThread = null;
		Iterator<?> it = threadMap.entrySet().iterator();
		java.util.Map.Entry<?, ?> entry = null;
		long begin = System.currentTimeMillis(), end;
		while(it.hasNext() && threadMap.size() > 0) {
			end = System.currentTimeMillis();
			if(end - begin > 1000 * 180)		break;
			try {
				entry = (java.util.Map.Entry<?, ?>)it.next();
				exitThread = (Thread)entry.getValue();
				if(exitThread != null && exitThread.isAlive()) {
					exitThread.join(100);
				} else {
					it.remove();
				}
			} catch(Exception e) {
				//这里不处理异常.异常一般是由同步导致的,这无关紧要.只要所有线程结束了就行.
				//加下try...catch...结构只是为了让这里不在控制台输出异常堆栈.
				e.printStackTrace();
			}
		}
	}
	
}
