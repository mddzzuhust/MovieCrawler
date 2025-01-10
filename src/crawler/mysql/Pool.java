package crawler.mysql;


import java.sql.*;

import crawler.config.Config;

public class Pool {

	static private Pool instance = null;    // 定义唯一实例
	private int maxConnect = 20;            // 最大连接数
	private int initConnect = 5;            // 保持连接数
	private String dbip; 					// 数据库IP
	private String dbsid;         			// 数据库SID
	private String user;       				// 用户名
	private String password;   				// 密码
	private Driver driver = null;           // 驱动变量
	DBConnectionPool pool = null;           // 连接池实例

	static synchronized public Pool getInstance() {
		if (instance == null) {
			instance = new Pool();
		}
		return instance;
	}

	private Pool() {
		Config config = Config.getInstance();
		this.dbip = config.getDbip();
    	this.dbsid = config.getDbsid();
    	this.user = config.getDbuser();
    	this.password = config.getDbpw();
    	
//		this.dbip = "192.168.1.230";
//		this.dbsid = "idcmedia";
//		this.user = "root";
//		this.password = "123456";
		
		loadDrivers();
		createPool();
	}
	
	public Pool clone() {
		return getInstance();
	}

	private void loadDrivers() {

		try {
			driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
			DriverManager.registerDriver(driver);
//			System.out.println("成功注册JDBC驱动程序");
		} catch (Exception e) {
			System.out.println("无法注册JDBC驱动程序:" + e);
		}
	}

	/*
	 *  创建数据库连接池
	 */
	private void createPool() {
		String url = "jdbc:mysql://" + dbip + ":3307/" + dbsid;
		pool = new DBConnectionPool(url, user, password, maxConnect, initConnect);
		if (pool != null) {
			System.out.println("创建连接池成功-[ " + pool.getNum() + " ]-个");
		} else {
			System.out.println("创建连接池失败");
		}
	}

	/*
	 *  获得一个可用的连接,如果没有则创建一个连接,且小于最大连接限制
	 */
	public Connection getConnection() {
		
		if (pool != null) {
			return pool.getConnection();
		}
		return null;
		
	}

	/*
	 *  获得一个连接,有时间限制
	 */
	public Connection getConnection(long time) {
		
		if (pool != null) {
			return pool.getConnection(time);
		}
		return null;
		
	}

	/*
	 *  将连接对象归还连接池
	 */
	public void freeConnection(Connection conn) {
		
		if (pool != null) {
			pool.freeConnection(conn);
		}
	}

	// 返回当前空闲连接数
	public int getNum() {
		return pool.getNum();
	}

	// 返回当前连接数
	public int getActiveNum() {
		return pool.getActiveNum();	
	}

	// 关闭所有连接,撤销驱动注册
	public synchronized void release() {

		pool.release(); // 关闭连接

		try { 
			DriverManager.deregisterDriver(driver); // 撤销驱动
//			System.out.println("撤销JDBC驱动程序 " + driver.getClass().getName());
		} catch (SQLException e) {
			System.out.println("无法撤销JDBC驱动程序的注册:" + driver.getClass().getName());
		}
	}
	
	public static void main(String []args)
	{
		Pool pool = Pool.getInstance();		
	}
	
	
}
