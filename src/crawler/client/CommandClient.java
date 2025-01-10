package crawler.client;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.regex.Pattern;

import crawler.RmiInterface;
import crawler.config.Config;
import crawler.crawler.Crawler;
import crawler.proxy.ProxyPool;
import crawler.sql.DBConn;

/**
 * 暂时未用到
 *
 */
public class CommandClient {

	static Config config;
	private RmiInterface master;
	
	public static void main(String[] args) {
		if(System.getSecurityManager()==null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		CommandClient client = new CommandClient();
		if(!client.init()) {
			System.out.println("初始化失败!");
			return;
		}
		
		client.startCrawler();
	}

	private boolean init() {
		config = Config.getInstance("property.xml");
		if(config == null) {
			System.err.println("读取配置文件失败!");
			return false;
		}
		if (config.isCheckhost()
				&& (config.getAllowDomain() == null ||
					config.getAllowDomain().isEmpty())) {
			System.err.println("限制爬行主机,但没有指定主机范围!");
			return false;
		}
		
		if(!testDatabase()) {
			System.err.println("数据库连接测试失败!");
			return false;
		}
		
		if(!testServer()) {
			System.err.println("服务端连接测试失败!");
			return false;
		}
		
		return true;
	}
	
	private boolean testDatabase() {
		DBConn dbconn = new DBConn(config.getDbip(), config.getDbsid(), config
				.getDbuser(), config.getDbpw());
		if (dbconn.getConnection() != null) {
			dbconn.closeDB();
			return true;
		}

		return false;
	}
	
	static final Pattern ipPattern = Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}$");
	private boolean testServer() {
		String ip = config.getServerip();
		if(!ipPattern.matcher(ip).matches()) {
			System.err.println("服务端ip地址格式错误");
			return false;
		}
		
		boolean ret = false;
		java.net.Socket socket = null;
		try {
			socket = new java.net.Socket(ip,CrawlerClient.SERVERPORT);
			if(socket != null) {
				socket.getOutputStream().write("CRAWLERSERVERTEST".getBytes());
				socket.getOutputStream().flush();
				socket.getOutputStream().close();
				ret = true;
			}
		} catch (Exception e) {
		} finally {
			if(socket != null)
			try {
				socket.close();
			} catch(Exception e) {
			}
		}
		return ret;
	}
	
	private void startCrawler() {
		if(config.isUseProxy())
			ProxyPool.parse("proxy.properties");
		
		Crawler.setRootPath(config.getImageSavePath());
		
		String hostaddress = config.getServerip();
		try {
			String rmiaddress = "rmi://" + hostaddress + ":5000/master";
			master = (RmiInterface)Naming.lookup(rmiaddress);
		} catch(Exception e) {
			System.err.println("初始化RMI调用失败.爬虫将退出!");
			return;
		}

		CrawlerClient crawler = new CrawlerClient();
		if (!crawler.initClient(master, config.getInitURLFile(), hostaddress,
				config.getThreads(), config.isIncrement())) {
			System.err.println("初始化爬虫客户端失败.");
			return;
		}
		
		if(!crawler.discoverServer()) {
			System.err.println("连接服务端失败,无法启动爬虫.");
			return;
		}
		
		if(crawler.getInitInformation()) {
			System.err.println("获取初始信息失败,无法启动爬虫.");
			return;
		}
		
		crawler.schedule();
	}

}
