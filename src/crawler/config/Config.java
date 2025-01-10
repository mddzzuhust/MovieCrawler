package crawler.config;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jdom.Document; // JDOM(Java Document Object Model) 解析XML文件
import org.jdom.Element;
import org.jdom.input.*;

/**
 * 配置文件解析类,以配置文件名为参数调用静态方法Config.getInstance()获取唯一实例
 * 获取数据库SID,用户名,密码,服务器IP,媒体保存路径,初始爬行URL文件,禁止爬行文件类型等信息
 * @author liuxue
 */
public class Config {

	private String configfile;
	
	private boolean useDatabase;
	private String dbip;
	private String dbsid;
	private String dbuser;
	private String dbpw;
	
	private String serverip;
	
	private boolean savedLocal;
	private boolean savedHbase;
	private String imageSavePath;
	private String initURLFile;
	private boolean useBDB;
	
	private boolean useTiming;
	private long periodTime;            //爬虫程序执行周期时间
	private String startTime;           //爬虫程序首次执行时间 
	private long runTime;               //爬虫每次运行时间
	

	private int scheduleTime;           //定时器时间,即URL发送,保存,信息输出的频率
	private int threads;                //爬虫线程数
	private int connectTimeOut;         //连接URL超时时间
	private int readTimeOut;            //读取数据超时时间
	
	private static int DEFAULT_SCHEDULETIME = 2;           //默认定时器间隔时间
	private static int DEFAULT_THREADS = 20;               //默认线程数
	private static int DEFAULT_CONNECT_TIMEOUT = 30000;    //默认连接超时时间(ms)
	private static int DEFAULT_READ_TIMEOUT = 30000;       //默认读取超时时间(ms)
	
	private boolean checkhost;          //是否限制爬行主机
	private List<String> allowDomain = null; //若限制爬行主机,则须给出爬行主机的域名范围
	private List<String> rejectDomain = null; //若排除爬行主机,则给出要跳过爬行的主机的域名
	private boolean useProxy;           //是否使用代理爬行
	private boolean increment;          //重新爬行|增量爬行
	
	private int maxImageSize;           //最大图像文件大小
	private int minImageSize;           //最小图像文件大小
	private int maxFilesPerDir;         //单个文件夹最多存放文件数量
	
	private static int DEFAULT_MAX_IMAGE_SIZE = -1;       //默认最大图像文件大小:不限制
	private static int DEFAULT_MIN_IMAGE_SIZE = 5120;    //默认最小图像文件大小:5KB
	private static int DEFAULT_MAX_FILES_PER_DIR = 2000;  //默认单个文件夹最多文件数量:2000
	
	private static String DEFAULT_START_TIME = "2014/08/01 00:00:00";
	private static long DEFAULT_PERIOD_TIME = 2592000;          //单位：s
	private static long DEFAULT_RUN_TIME = 10800;               //单位：s 
	
	private HashSet<String> allowImageType = new HashSet<String>();
	
	private static Config instance = null;    // 定义唯一实例
	
	private Config() {
		
	}
	
	private Config(String configfile) {
		this.configfile = configfile;
		parseConfig();
	}
	//覆盖clone方法,防止通过该方法获得Config对象引用
	protected Config clone() {
		return getInstance();
	}
	
	//获取单一实例(设计模式：单例模式)
	static synchronized public Config getInstance(String configfile) {
		if (instance == null) {
			instance = new Config(configfile);
		}
		return instance;
	}
	
	static synchronized public Config getInstance() {
		if (instance != null)
			return instance;
		return getInstance("property.xml");
	}
	
	/**
	 * 解析配置文件内容
	 * @param configfile 配置文件名. 默认为property.xml文件
	 */
	@SuppressWarnings("unchecked")
	public void parseConfig() {
		SAXBuilder builder = new SAXBuilder();
		Reader in = null;
		Document doc;
		try {
			in = new FileReader(configfile);
			doc = builder.build(in);
		} catch (Exception e) {
			return;
		} finally {
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {	}
		}
		
		Element root = doc.getRootElement();
		
		//从配置文件获取数据库信息
		Element database = root.getChild("database");
		useDatabase = database.getChildTextTrim("use").equalsIgnoreCase("true");
		dbip = database.getChildTextTrim("ip");
		dbsid = database.getChildTextTrim("sid");
		dbuser = database.getChildTextTrim("user");
		dbpw = database.getChildTextTrim("password");
		
		Element server = root.getChild("server");
		serverip = server.getChildTextTrim("ip");
		
		Element client = root.getChild("client");
		savedLocal = client.getChildTextTrim("savedLocal").equalsIgnoreCase("true");
		savedHbase = client.getChildTextTrim("savedHbase").equalsIgnoreCase("true");
		imageSavePath = client.getChildTextTrim("imageSavePath");
		initURLFile = client.getChildTextTrim("initURLFile");
		useBDB = client.getChildTextTrim("useBDB").equalsIgnoreCase("true");
		
		
		Element timer = root.getChild("Timer");
		useTiming = timer.getChildText("useTiming").equalsIgnoreCase("true");
		try {
			startTime = timer.getChildTextTrim("startTime");
			periodTime = Long.parseLong(timer.getChildText("periodTime"))*1000;
			runTime = Long.parseLong(timer.getChildText("runTime"))*1000;
		} catch (Exception e) {
			startTime = DEFAULT_START_TIME;
			periodTime = DEFAULT_PERIOD_TIME;
			runTime = DEFAULT_RUN_TIME;
		}
		
		Element thread = root.getChild("thread");
		Element connect = root.getChild("connect");
		Element read = root.getChild("read");
		Element schedule = root.getChild("schedule");
		try {
			threads = Integer.parseInt(thread.getChildTextTrim("number"));
			connectTimeOut = Integer.parseInt(connect.getChildTextTrim("timeout"));
			readTimeOut = Integer.parseInt(read.getChildTextTrim("timeout"));
			scheduleTime = Integer.parseInt(schedule.getChildTextTrim("time"));
		} catch (Exception e) {
			threads = DEFAULT_THREADS;
			connectTimeOut = DEFAULT_CONNECT_TIMEOUT;
			readTimeOut = DEFAULT_READ_TIMEOUT;
			scheduleTime = DEFAULT_SCHEDULETIME;
		}
		
		Element download = root.getChild("download");
		try {
			maxImageSize = Integer.parseInt(download.getChildTextTrim("maxImageSize"));
			minImageSize = Integer.parseInt(download.getChildTextTrim("minImageSize"));
			maxFilesPerDir = Integer.parseInt(download.getChildTextTrim("maxFilesPerDir"));
		} catch (Exception e) {
			maxImageSize = DEFAULT_MAX_IMAGE_SIZE;
			minImageSize = DEFAULT_MIN_IMAGE_SIZE;
			maxFilesPerDir = DEFAULT_MAX_FILES_PER_DIR;
		}
		
		Element proxy = root.getChild("proxy");
		useProxy = proxy.getChildTextTrim("use").trim().equals("true");
		
		Element method = root.getChild("increment");
		increment = method.getChildTextTrim("value").trim().equals("true");
		
		Element element = root.getChild("image");
		List<Element> type = element.getChildren();
		for(int i = 0; i < type.size(); i++) {
			allowImageType.add(type.get(i).getTextTrim());
		}
		
		Element host = root.getChild("host");
		checkhost = host.getChildTextTrim("check").trim().equals("true");
		if(checkhost) {
			type = host.getChildren("allow-domain");
			allowDomain = new ArrayList<String>(type.size());
			for(int i = 0; i < type.size(); i++) {
				String domain = type.get(i).getTextTrim();
				if(!allowDomain.contains(domain))
					allowDomain.add(domain);
			}
		}
		type = host.getChildren("reject-domain");
		rejectDomain = new ArrayList<String>(type.size());
		for(int i = 0; i < type.size(); i++) {
			String domain = type.get(i).getTextTrim();
			if(!rejectDomain.contains(domain))
				rejectDomain.add(domain);
		}
		
		instance = new Config();/*
		instance.setUseDatabase(useDatabase);
		instance.setDbip(dbip);
		instance.setDbsid(dbsid);
		instance.setDbuser(dbuser);
		instance.setDbpw(dbpw);
		instance.setServerip(serverip);
		instance.setImageSavePath(imageSavePath);
		instance.setInitURLFile(initURLFile);
		instance.setCheckhost(checkhost);
		instance.setUseProxy(useProxy);
		instance.setThreads(threads);
		instance.setScheduleTime(scheduleTime);
		instance.setIncrement(increment);
		instance.setAllowImageType(allowImageType);*/
		
		try {
			in.close();
		} catch (IOException e) {	}
	}

	public boolean isUseDatabase() {
		return useDatabase;
	}

	public void setUseDatabase(boolean useDatabase) {
		this.useDatabase = useDatabase;
	}

	public String getDbip() {
		return dbip;
	}

	public void setDbip(String dbip) {
		this.dbip = dbip;
	}

	public String getDbsid() {
		return dbsid;
	}

	public void setDbsid(String dbsid) {
		this.dbsid = dbsid;
	}

	public String getDbuser() {
		return dbuser;
	}

	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}

	public String getDbpw() {
		return dbpw;
	}

	public void setDbpw(String dbpw) {
		this.dbpw = dbpw;
	}

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public boolean isSavedLocal() {
		return savedLocal;
	}

	public void setSavedLocal(boolean b) {
		this.savedLocal = b;
	}
	
	public boolean isSavedHbase() {
		return savedHbase;
	}

	public void setSavedHbase(boolean b) {
		this.savedHbase = b;
	}
	
	public String getImageSavePath() {
		return imageSavePath;
	}

	public void setImageSavePath(String imageSavePath) {
		this.imageSavePath = imageSavePath;
	}

	public String getInitURLFile() {
		return initURLFile;
	}

	public void setInitURLFile(String initURLFile) {
		this.initURLFile = initURLFile;
	}
	
	public boolean isTiming(){
		return useTiming;
	}
	
	public void setTiming(boolean b){
	     this.useTiming = b;
	}
	
	public long getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(long periodTime) {
		this.periodTime = periodTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public long getRunTime() {
		return runTime;
	}
	
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}
	
	public boolean isUseBDB() {
		return useBDB;
	}

	public void setUseBDB(boolean b) {
		this.useBDB = b;
	}
	
	public int getScheduleTime() {
		return scheduleTime;
	}
	
	public void setScheduleTime(int scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public boolean isCheckhost() {
		return checkhost;
	}

	public void setCheckhost(boolean checkhost) {
		this.checkhost = checkhost;
	}
	
	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
	
	public boolean isIncrement() {
		return increment;
	}

	public void setIncrement(boolean increment) {
		this.increment = increment;
	}
	
	public int getThreads() {
		return threads;
	}
	
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public int getReadTimeOut() {
		return readTimeOut;
	}

	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public int getMaxImageSize() {
		return maxImageSize;
	}

	public void setMaxImageSize(int maxImageSize) {
		this.maxImageSize = maxImageSize;
	}

	public int getMinImageSize() {
		return minImageSize;
	}

	public void setMinImageSize(int minImageSize) {
		this.minImageSize = minImageSize;
	}

	public int getMaxFilesPerDir() {
		return maxFilesPerDir;
	}

	public void setMaxFilesPerDir(int maxFilesPerDir) {
		this.maxFilesPerDir = maxFilesPerDir;
	}

	public HashSet<String> getAllowImageType() {
		return allowImageType;
	}

	public void setAllowImageType(HashSet<String> image) {
		this.allowImageType = image;
	}
	
	public List<String> getAllowDomain() {
		return allowDomain;
	}

	public void setAllowDomain(List<String> domain) {
		this.allowDomain = domain;
	}

	public List<String> getRejectDomain() {
		return rejectDomain;
	}

	public void setRejectDomain(List<String> rejectDomain) {
		this.rejectDomain = rejectDomain;
	}

	// 测试
	public static void main(String[] args) {
		Config config = Config.getInstance("property.xml");
		System.out.println(config.isUseBDB());
		System.out.println(config.getDbip());
		System.out.println(config.getDbsid());
		//System.out.println(config.isSavedLocal());
		System.out.println(config.getImageSavePath());
	}
}
