package crawler.client;

import java.rmi.*;
//import java.sql.Timestamp;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.regex.Pattern;
import java.net.UnknownHostException;

import javax.swing.event.ListDataEvent;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import crawler.RmiInterface;
import crawler.config.Config;
import crawler.crawler.Crawler;
import crawler.mysql.Pool;
import crawler.timer.CrawlerTimerConfig;
import crawler.proxy.ProxyPool;



import java.rmi.Naming;

/**
 * 爬虫客户端,运行类.
 */
public class Client implements Runnable{
	
	private JFrame mainFrame;
	
	private JPanel database;               //数据库设置
    private JPanel crawlerset;             //爬虫设置
    private JPanel mainPane;               //主面板

    private JLabel dbip;                   //数据库IP
    private JLabel dbsid;                  //数据库SID
    private JLabel dbuser;                 //数据库用户名
    private JLabel dbpw;                   //数据库密码
    private JLabel serverip;               //服务器IP
    private JLabel initURLFile;            //起始URL文件
    private JLabel saveWay;					// 图片保存方式
    private JLabel savePath;               //下载作品保存路径
    
    private JScrollPane listScrollPane;    //消息显示下拉条
    private JCheckBox local;				// 图片存在本地文件系统
    private JCheckBox hbase;				// 图片存储在Hbase
    
    private JTextField dbipTxt;
    private JTextField dbsidTxt;
    private JTextField dbuserTxt;
    private JPasswordField dbpwTxt;
    
    private JTextField serveripTxt;
    private JTextField initURLTxt;
    private JTextField savePathTxt;

    private JButton testdbBtn;
    private JButton testServerBtn;
    private JButton initURLBtn;
    private JButton getPathBtn;
    private JButton startBtn;
    private JButton stopBtn;
    
    private JFileChooser fileset;
    @SuppressWarnings("rawtypes")
	private JList messageList;
    @SuppressWarnings("rawtypes")
	public static DefaultListModel messageListModel;
    public static String databaseip;
    public static String databasesid;
    public static String databaseuser;
    public static String databasepw;
    
    private CrawlerClient crawler;
    private RmiInterface master;
    
    public static String file_separator = System.getProperty("file.separator");
    public static Config config = Config.getInstance();
    private static boolean isSavedLocal = Config.getInstance().isSavedLocal();
    private static boolean isSavedHbase = Config.getInstance().isSavedHbase();
    private static boolean isTiming = Config.getInstance().isTiming();
    private static long runTime;
    
    boolean stopFlag = false;   //用于表示定时运行时，结束任务操作是人工操作还是自动操作
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {

		mainFrame = new JFrame("电影海报信息爬虫客户端");
		
		local = new JCheckBox("本地文件系统");
		hbase = new JCheckBox("Hbase数据库");
		
		database  = new JPanel();
	    crawlerset  = new JPanel();
	    mainPane = new JPanel();

	    dbip = new JLabel("数据库IP:");
	    dbsid = new JLabel("SID:");
	    dbuser = new JLabel("用户名:");
	    dbpw = new JLabel("密码:");
	    serverip = new JLabel("服务器IP:");
	    initURLFile = new JLabel("起始链接:");
	    saveWay = new JLabel(" 保存方式:");
	    savePath = new JLabel("保存目录:");
	    
	    dbip.setPreferredSize(new java.awt.Dimension(58, 15));
	    dbip.setHorizontalAlignment(SwingConstants.RIGHT); 
	    dbsid.setPreferredSize(new java.awt.Dimension(58, 15));
	    dbsid.setHorizontalAlignment(SwingConstants.RIGHT);
	    dbuser.setPreferredSize(new java.awt.Dimension(58, 15));
	    dbuser.setHorizontalAlignment(SwingConstants.RIGHT);
	    dbpw.setPreferredSize(new java.awt.Dimension(58, 15));
	    dbpw.setHorizontalAlignment(SwingConstants.RIGHT);
	    
	    if(isTiming) {
	    	dbipTxt = new JTextField(CrawlerTimerConfig.dbip,18);
	        dbsidTxt = new JTextField(CrawlerTimerConfig.dbsid,18);
			dbuserTxt = new JTextField(CrawlerTimerConfig.dbuser,18);
			dbpwTxt = new JPasswordField(CrawlerTimerConfig.dbpw,18);
			serveripTxt = new JTextField(CrawlerTimerConfig.hostaddress,18);
			initURLTxt = new JTextField(CrawlerTimerConfig.filename,18);
			savePathTxt = new JTextField(CrawlerTimerConfig.strPath,18);
	    }else {
		    dbipTxt = new JTextField(config.getDbip(),18);
		    dbsidTxt = new JTextField(config.getDbsid(),18);
		    dbuserTxt = new JTextField(config.getDbuser(),18);
		    dbpwTxt = new JPasswordField(config.getDbpw(),18);
		    serveripTxt = new JTextField(config.getServerip(),18);
		    initURLTxt = new JTextField(config.getInitURLFile(),18);
		    savePathTxt = new JTextField(config.getImageSavePath(),18);
	    }
	    testdbBtn = new JButton("测试");
	    testServerBtn = new JButton("测试");
	    initURLBtn = new JButton("选择");
	    getPathBtn = new JButton("选择");
	    startBtn = new JButton("开始");
	    stopBtn = new JButton("结束");
	    testdbBtn.addActionListener(new java.awt.event.ActionListener(){
	    	public void actionPerformed(java.awt.event.ActionEvent event){
	    		testDatabase(true);
	    	}
	    });
	    testServerBtn.addActionListener(new java.awt.event.ActionListener(){
	    	public void actionPerformed(java.awt.event.ActionEvent event){
	    		testServer(true);
	    	}
	    });
	    initURLBtn.addActionListener(new java.awt.event.ActionListener(){
	    	public void actionPerformed(java.awt.event.ActionEvent event){
	    		getInitURLFile();
	    	}
	    });
	    getPathBtn.addActionListener(new java.awt.event.ActionListener(){
	    	public void actionPerformed(java.awt.event.ActionEvent event){
	    		getSavePath();
	    	}
	    });
	    startBtn.addActionListener(new java.awt.event.ActionListener(){
	    	public void actionPerformed(java.awt.event.ActionEvent event){
	    		/*try {
	    			// 简单地定时启动，供测试使用。
	    			System.out.println(new Timestamp(System.currentTimeMillis()));
					Thread.sleep(60* 60 * 1000);
					System.out.println(new Timestamp(System.currentTimeMillis()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
	    		startCrawler();
	    	}
	    });
	    stopBtn.addActionListener(new java.awt.event.ActionListener(){
	    	public void actionPerformed(java.awt.event.ActionEvent event){
	    		stopFlag = true;
	    		stopCrawler();
	    	}
	    });
	    local.addItemListener(new java.awt.event.ItemListener(){
	    	public void itemStateChanged(java.awt.event.ItemEvent event){
	    		isSavedLocal = local.isSelected();
	    		if(isSavedLocal){
	    			savePath.setEnabled(true);
	    			savePathTxt.setEnabled(true);
	    			getPathBtn.setEnabled(true);
	    		} else{
	    			savePath.setEnabled(false);
	    			savePathTxt.setEnabled(false);
	    			getPathBtn.setEnabled(false);
	    		}
	    		Config.getInstance().setSavedLocal(isSavedLocal);
	    	}
	    });
	    hbase.addItemListener(new java.awt.event.ItemListener(){
	    	public void itemStateChanged(java.awt.event.ItemEvent event){
	    		isSavedHbase = hbase.isSelected();
	    		Config.getInstance().setSavedHbase(isSavedHbase);
	    	}
	    });
	    
	    database.setLayout(new java.awt.GridLayout(5,1));
	    database.setBorder(javax.swing.BorderFactory.createTitledBorder("数据库设置:"));
	    JPanel database1 = new JPanel();
	    JPanel database2 = new JPanel();
	    JPanel database3 = new JPanel();
	    JPanel database4 = new JPanel();
	    JPanel database5 = new JPanel();
	    database1.add(dbip);
	    database1.add(dbipTxt);
	    database2.add(dbsid);
	    database2.add(dbsidTxt);
	    database3.add(dbuser);
	    database3.add(dbuserTxt);
	    database4.add(dbpw);
	    database4.add(dbpwTxt);
	    database5.add(testdbBtn);
	    database.add(database1);
	    database.add(database2);
	    database.add(database3);
	    database.add(database4);
	    database.add(database5);
	    
	    crawlerset.setLayout(new java.awt.GridLayout(5,1));
	    crawlerset.setBorder(javax.swing.BorderFactory.createTitledBorder("爬行设置:"));
	    JPanel crawlerset1 = new JPanel();
	    JPanel crawlerset2 = new JPanel();
	    JPanel crawlerset3 = new JPanel();
	    JPanel crawlerset4 = new JPanel();
	    JPanel crawlerset5 = new JPanel();
	    crawlerset2.setLayout(new java.awt.GridLayout(1,4));
	    crawlerset1.add(serverip);
	    crawlerset1.add(serveripTxt);
	    crawlerset1.add(testServerBtn);
	    crawlerset2.add(saveWay);
	    crawlerset2.add(local);
	    crawlerset2.add(hbase);
	    crawlerset3.add(initURLFile);
	    crawlerset3.add(initURLTxt);
	    crawlerset3.add(initURLBtn);
	    crawlerset4.add(savePath);
	    crawlerset4.add(savePathTxt);
	    crawlerset4.add(getPathBtn);
	    crawlerset5.add(startBtn);
	    crawlerset5.add(stopBtn);
		crawlerset.add(crawlerset1);
		crawlerset.add(crawlerset2);
		crawlerset.add(crawlerset3);
		crawlerset.add(crawlerset4);
		crawlerset.add(crawlerset5);
		crawlerset4.setSize(crawlerset1.getPreferredSize());
		
		mainPane.add(database);
		mainPane.add(crawlerset);
		
		messageListModel = new DefaultListModel();
		messageList = new JList(messageListModel);
		messageList.setSelectionBackground(java.awt.Color.cyan);
        messageList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        messageList.setVisibleRowCount(10);
		listScrollPane = new JScrollPane(messageList);
		messageListModel.addListDataListener(new javax.swing.event.ListDataListener() {

			@Override
			public void contentsChanged(ListDataEvent e) {
				
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				javax.swing.JScrollBar sBar = listScrollPane.getVerticalScrollBar();
				sBar.setValue(sBar.getMaximum());
				sBar.scrollRectToVisible(new java.awt.Rectangle(sBar.getWidth(), sBar.getHeight()));
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				
			}
			
		});
//		listScrollPane.scrollRectToVisible(new Rectangle(0,messageListModel.getSize()));
		
		mainFrame.getContentPane().add(mainPane,BorderLayout.NORTH);
		mainFrame.getContentPane().add(listScrollPane,BorderLayout.CENTER);
		
//		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(CrawlerClient.bfilter != null)
					CrawlerClient.bfilter.save();
				System.exit(0);
			}
		});
        mainFrame.pack();
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null); //使窗口居中
		mainFrame.setVisible(true);
		stopBtn.setEnabled(false);
		
		if(!isSavedLocal){
			savePath.setEnabled(false);
			savePathTxt.setEnabled(false);
			getPathBtn.setEnabled(false);
		} else {
			local.setSelected(true);
		}
		if(isSavedHbase){
			hbase.setSelected(true);
		}
		
		if (!config.isUseDatabase()) // 若配置文件设置不使用数据库,则禁止该测试按钮
			testdbBtn.setEnabled(false);
		
		if (Client.isTiming)    //自动爬行
		  startCrawler();
	}
	
	
	
	private boolean testDatabase(boolean showMessage) {
		config.setDbip(dbipTxt.getText().trim());
		config.setDbsid(dbsidTxt.getText().trim());
		config.setDbuser(dbuserTxt.getText().trim());
		config.setDbpw(String.valueOf(dbpwTxt.getPassword()));
		java.sql.Connection conn = Pool.getInstance().getConnection();
		if(conn == null) {
			if(showMessage) 
				JOptionPane.showMessageDialog(null,	"无法连接数据库,请检查输入参数.", "连接失败", JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			//Pool.getInstance().freeConnection(conn);
			if(showMessage)
				JOptionPane.showMessageDialog(null,	"测试数据库成功,可以使用.",
						"测试成功", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
	}
	
	static final Pattern ipPattern = Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}$");
	private boolean testServer(boolean showMessage) {
		String ip = this.serveripTxt.getText().trim();
		if(!ipPattern.matcher(ip).matches()) {
			if(showMessage)
				JOptionPane.showMessageDialog(null,	"IP地址格式错误",
						"输入错误", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		boolean ret = false;
		java.net.Socket socket = null;
		java.io.OutputStream os = null;
		try {
			socket = new java.net.Socket(ip, CrawlerClient.SERVERPORT);
			if(socket != null) {
				if(showMessage)
					JOptionPane.showMessageDialog(null,	"测试服务端成功,可以使用.",
							"测试成功", JOptionPane.INFORMATION_MESSAGE);
				os = socket.getOutputStream();
				os.write("CRAWLERSERVERTEST".getBytes());
				os.flush();
				ret = true;
			}
		} catch (UnknownHostException e) {
			if(showMessage)
				JOptionPane.showMessageDialog(null,	"未发现服务端",
						"测试失败", JOptionPane.ERROR_MESSAGE);
			ret = false;
		} catch (IOException e) {
			if(showMessage)
				JOptionPane.showMessageDialog(null,	"网络错误,请检查.",
						"测试失败", JOptionPane.ERROR_MESSAGE);
			ret = false;
		} finally {
			try {
				if(os != null)
				os.close();
				if(socket != null)
				socket.close();
			} catch (IOException e) {
				ret = false;
			}
		}
		return ret;
	}

	private void getInitURLFile() {
		fileset = new JFileChooser(System.getProperty("user.dir"));
		fileset.setDialogType(JFileChooser.OPEN_DIALOG);
		fileset.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(fileset.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			String filename = fileset.getSelectedFile().getPath();
			this.initURLTxt.setText(filename);
			config.setInitURLFile(filename);
			System.out.println("filename:"+filename);
		}
	}

	private void getSavePath() {
		fileset = new JFileChooser(System.getProperty("user.dir"));
		fileset.setDialogType(JFileChooser.OPEN_DIALOG);
		fileset.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(fileset.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			String savepath = fileset.getSelectedFile().getPath();
			this.savePathTxt.setText(savepath);
			config.setImageSavePath(savepath);
		}
	}

	private void startCrawler() {
		
		if(config == null) {
			System.err.println("读取配置文件失败!");
			return;
		}
		
		if(config.isUseProxy()) {
			ProxyPool.parse("proxy.properties");
		}
		
		// 测试数据库连接
		if(config.isUseDatabase() && !testDatabase(false)) {
			JOptionPane.showMessageDialog(null,	"数据库测试失败",
    				"连接失败", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 测试客户端到服务端的连接
		if(!testServer(false)) {
			JOptionPane.showMessageDialog(null,	"服务端测试失败",
    				"测试失败", JOptionPane.ERROR_MESSAGE);
			return;
		}
	
	
		// 更新配置
		String strPath;               
		String hostaddress;             //服务器ip
		String filename;                 //初始链接文件名
		//isTiming是干什么用的？
		if(isTiming){
			strPath = CrawlerTimerConfig.strPath;
			hostaddress = CrawlerTimerConfig.hostaddress;
			filename = CrawlerTimerConfig.filename;
			runTime = CrawlerTimerConfig.runTime;
			
		}else{
			strPath = this.savePathTxt.getText().trim();
			hostaddress = this.serveripTxt.getText().trim();
			filename = this.initURLTxt.getText().trim();   
		}
			
		config.setImageSavePath(strPath);
		config.setServerip(this.serveripTxt.getText().trim());
		Crawler.setRootPath(strPath);

		
		try {
			String rmiaddress = "rmi://" + hostaddress + ":5000/master";
			master = (RmiInterface)Naming.lookup(rmiaddress);
		} catch(Exception e) {
			System.out.println("初始化RMI调用失败.爬虫将退出!");
			e.printStackTrace();
			return;
		}
		
		crawler = new CrawlerClient();
		// 读取初始化链接
		
		if (!crawler.initClient(master, filename, hostaddress,
				config.getThreads(), config.isIncrement())) {
			System.err.println("初始化爬虫客户端失败.");
			return;
		}

		if(!crawler.discoverServer()) {
			System.err.println("连接服务端失败,无法启动爬虫.");
			return;
		}
		
		if(!crawler.getInitInformation()) {
			System.err.println("获取初始信息失败,无法启动爬虫.");
			return;
		}
		
		this.testdbBtn.setEnabled(false);
		this.testServerBtn.setEnabled(false);
		this.initURLBtn.setEnabled(false);
		this.getPathBtn.setEnabled(false);
		this.startBtn.setEnabled(false);
		this.stopBtn.setEnabled(true);
		Client.messageListModel.clear();
	    long startTime = System.currentTimeMillis();
		Thread thread = new Thread(this);
		thread.start();
		if(Client.isTiming){
			while(System.currentTimeMillis() - startTime < runTime)
				;
			if(!stopFlag)
			  stopCrawler();
		    mainFrame.dispose();			
		}
	}
	
	private void stopCrawler() {
		crawler.stopCrawler();
		
		if (config.isUseDatabase()) // 若配置文件设置不使用数据库,则禁止该测试按钮
			testdbBtn.setEnabled(true);
		
		this.testServerBtn.setEnabled(true);
		this.initURLBtn.setEnabled(true);
		this.getPathBtn.setEnabled(true);
		this.startBtn.setEnabled(true);
		this.stopBtn.setEnabled(false);
		crawler = null;
		System.gc();
	}
	
	public void run() {
		crawler.schedule();
	}
	
	@SuppressWarnings("unchecked")
	public static void showMessage(Object message) {
		Client.messageListModel.insertElementAt(message.toString(),Client.messageListModel.getSize());
	}
	
	public static void main(String[] args) {

//		if(System.getSecurityManager()==null) {
//			System.setSecurityManager(new SecurityManager());
//		}
		Client client = new Client();
		client.initComponents();
	}	
}
