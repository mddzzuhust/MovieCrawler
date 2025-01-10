package crawler.timer;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.regex.Pattern;
import java.awt.*;

import javax.swing.*;

import crawler.client.CrawlerClient;
import crawler.config.Config;
import crawler.proxy.ProxyPool;
import crawler.sql.Pool;

/**
 * 功能：执行定时任务
 * @author xiaorui
*/
public class CrawlerTimer{
	

    
    private JFrame mainFrame;
	private JPanel dataBase;               //数据库设置
    private JPanel crawlerSet;             //爬虫设置
    private JPanel timerSet;               //定时器设置
    
    private JButton testdbBtn = new JButton("测试");
    private JButton testServerBtn = new JButton("测试");
    private JButton initURLBtn = new JButton("选择");
    private JButton getPathBtn = new JButton("选择");
    private JButton startBtn = new JButton("开始");
    private JButton stopBtn = new JButton("结束");
    
    private JFileChooser fileset;
    
    private JLabel dbip;                   //数据库IP
    private JLabel dbsid;                  //数据库SID
    private JLabel dbuser;                 //数据库用户名
    private JLabel dbpw;                   //数据库密码
    private JLabel serverip;               //服务器IP
    private JLabel initURLFile;            //起始URL文件
    private JLabel savePath;               //下载作品保存路径
    private JLabel startClock;             //起始时间
    private JLabel elapsedTime;                //爬虫运行时间
    private JLabel cycleTime;              //运行频率
    private JLabel saveWay;
    
    private JTextField dbipTxt;
    private JTextField dbsidTxt;
    private JTextField dbuserTxt;
    private JPasswordField dbpwTxt;
    
    private JTextField serveripTxt;
    private JTextField initURLTxt;
    private JTextField savePathTxt;
       
    private JTextField startClockTxt;
    private JTextField runTimeTxt;
    private JTextField cycleTimeTxt;
    
    private JCheckBox local;				// 图片存在本地文件系统
    private JCheckBox hbase;				// 图片存储在Hbase
    
    private static boolean isSavedLocal = Config.getInstance().isSavedLocal();
    private static boolean isSavedHbase = Config.getInstance().isSavedHbase();
    
    public static String file_separator = System.getProperty("file.separator");
    public static Config config = Config.getInstance();
    private static boolean isTiming = Config.getInstance().isTiming();
    private static String startTime = Config.getInstance().getStartTime();
    private static long periodTime = Config.getInstance().getPeriodTime()*1000;
    private static long runTime = Config.getInstance().getRunTime()*1000;

    public CrawlerTimer(){

		local = new JCheckBox("本地文件系统");
		hbase = new JCheckBox("Hbase数据库");
    	
    	dbip = new JLabel("数据库IP:");
	    dbsid = new JLabel("SID:");
	    dbuser = new JLabel("用户名:");
	    dbpw = new JLabel("密码:");
	    serverip = new JLabel("服务器IP:");
	    initURLFile = new JLabel("起始链接:");
	    savePath = new JLabel("保存目录:");
	    saveWay = new JLabel(" 保存方式:");
	    startClock = new JLabel("起始时间(单位:秒):");             
	    elapsedTime = new JLabel("运行时间(单位:秒):");               
	    cycleTime = new JLabel("周期频率(单位:秒):");              
	    
  	    dbipTxt = new JTextField(config.getDbip(),18);
  	    dbsidTxt = new JTextField(config.getDbsid(),18);
  	    dbuserTxt = new JTextField(config.getDbuser(),18);
  	    dbpwTxt = new JPasswordField(config.getDbpw(),18);
  	    serveripTxt = new JTextField(config.getServerip(),18);
  	    initURLTxt = new JTextField(config.getInitURLFile(),18);
  	    savePathTxt = new JTextField(config.getImageSavePath(),18);
  	    startClockTxt = new JTextField(config.getStartTime(),18);
  	    runTimeTxt = new JTextField(String.valueOf(config.getRunTime()/1000),18);
  	    cycleTimeTxt = new JTextField(String.valueOf(config.getPeriodTime()/1000),18);

  	    dbip.setPreferredSize(new java.awt.Dimension(58, 15));
	    dbip.setHorizontalAlignment(SwingConstants.RIGHT); 
	    dbsid.setPreferredSize(new java.awt.Dimension(58, 15));
	    dbsid.setHorizontalAlignment(SwingConstants.RIGHT);
	    dbuser.setPreferredSize(new java.awt.Dimension(58, 15));
	    dbuser.setHorizontalAlignment(SwingConstants.RIGHT);
	    dbpw.setPreferredSize(new java.awt.Dimension(58, 15));
	    dbpw.setHorizontalAlignment(SwingConstants.RIGHT);

  	    
	    
	    
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
	    
    	dataBase = new JPanel();
    	dataBase.setLayout(new GridLayout(5,1));
    	dataBase.setBorder(javax.swing.BorderFactory.createTitledBorder("数据库设置:"));
         
	    JPanel dataBase1 = new JPanel();
	    JPanel dataBase2 = new JPanel();
	    JPanel dataBase3 = new JPanel();
	    JPanel dataBase4 = new JPanel();
	    JPanel dataBase5 = new JPanel();
         
	    
	    dataBase1.add(dbip);
	    dataBase1.add(dbipTxt);
	    dataBase2.add(dbsid);
	    dataBase2.add(dbsidTxt);
	    dataBase3.add(dbuser);
	    dataBase3.add(dbuserTxt);
	    dataBase4.add(dbpw);
	    dataBase4.add(dbpwTxt);
	    dataBase5.add(testdbBtn,BorderLayout.CENTER);

    	dataBase.add(dataBase1);
    	dataBase.add(dataBase2);
    	dataBase.add(dataBase3);
    	dataBase.add(dataBase4);
    	dataBase.add(dataBase5);
    	
    	crawlerSet = new JPanel();
    	crawlerSet.setLayout(new java.awt.GridLayout(4,1));
	    crawlerSet.setBorder(javax.swing.BorderFactory.createTitledBorder("爬行设置:"));
	    JPanel crawlerset1 = new JPanel();
	    JPanel crawlerset2 = new JPanel();
	    JPanel crawlerset3 = new JPanel();
	    JPanel crawlerset4 = new JPanel();

	    
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
   
	    
	    
	    
	    

		crawlerSet.add(crawlerset1);
		crawlerSet.add(crawlerset2);
		crawlerSet.add(crawlerset3);
		crawlerSet.add(crawlerset4);
		
		
		
    	timerSet = new JPanel();
    	timerSet.setLayout(new GridLayout(4,1));
    	timerSet.setBorder(javax.swing.BorderFactory.createTitledBorder("定时任务设置:"));
    	JPanel timerSet1 = new JPanel();
    	JPanel timerSet2 = new JPanel();
    	JPanel timerSet3 = new JPanel();
    	JPanel timerSet4 = new JPanel();
    	
    	timerSet1.add(startClock);
    	timerSet1.add(startClockTxt);
    	timerSet2.add(elapsedTime);
    	timerSet2.add(runTimeTxt);
    	timerSet3.add(cycleTime);
    	timerSet3.add(cycleTimeTxt);
    	timerSet4.add(startBtn);
    	timerSet4.add(stopBtn);
    	
    	timerSet.add(timerSet1);
    	timerSet.add(timerSet2);
    	timerSet.add(timerSet3);
    	timerSet.add(timerSet4);
    	
    	
    	mainFrame = new JFrame();
    	mainFrame.setResizable(false);
    
    	
    	mainFrame.add(dataBase,BorderLayout.WEST);
    	mainFrame.add(crawlerSet,BorderLayout.CENTER);
    	mainFrame.add(timerSet,BorderLayout.EAST);
    	
       	mainFrame.setTitle("爬虫定时任务管理器");
    	mainFrame.setSize(1000, 220);
    	
    	mainFrame.setLocationRelativeTo(null);
    	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainFrame.setVisible(true);
    	
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
    	
    	if(!config.isUseDatabase()) // 若配置文件设置不使用数据库,则禁止该测试按钮
			testdbBtn.setEnabled(false);
    	
    	if(!isTiming)
    		startBtn.setEnabled(false);
    	
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
		try{
	 
			if(isTiming){
				
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
				
				CrawlerTimerConfig.dbip = config.getDbip();
				CrawlerTimerConfig.dbsid = config.getDbsid();
				CrawlerTimerConfig.dbuser = config.getDbuser();
				CrawlerTimerConfig.dbpw = config.getDbpw();
				 
				CrawlerTimerConfig.filename = this.initURLTxt.getText().trim();
				CrawlerTimerConfig.strPath = this.savePathTxt.getText().trim();      //文件保存路径
				CrawlerTimerConfig.hostaddress = this.serveripTxt.getText().trim();  //服务器ip
				
				CrawlerTimerConfig.startClock = startClockTxt.getText().trim();
				CrawlerTimerConfig.runTime = Long.parseLong(runTimeTxt.getText().trim())*1000;
				CrawlerTimerConfig.periodTime = Long.parseLong(cycleTimeTxt.getText().trim())*1000;
	
				
				Timer timer = new Timer();	
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
				Date date = sdf.parse(startTime);
				timer.schedule(new MyTask(),date,periodTime);
			}else{
				System.out.println("请在配置文件中将\"usingTiming\"字段设置为true！");
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    
	private void stopCrawler() {
		System.gc();
		mainFrame.dispose();
	}

	public static void main(String[] args) throws ParseException {
			// TODO Auto-generated method stub
//		if(isTiming){
//			Timer timer = new Timer();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
//			Date date = sdf.parse(startTime);
//			timer.schedule(new MyTask(),date,periodTime);
//		}else{
//			System.out.println("请在配置文件中将\"usingTiming\"字段设置为true！");
//		}
		CrawlerTimer tr = new CrawlerTimer();
	}
}
