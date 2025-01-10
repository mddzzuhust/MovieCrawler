package crawler.sql;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DBConnectionPool {

	private String url;
	private String user;
	private String password;
	private int maxConn;
	private static int num = 0;       // 空闲连接数
	private static int activeNum = 0; // 当前连接数
	private Vector<Connection> freeConnections;

	public DBConnectionPool(String url, String user, String password, int maxConn, int initConn) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.maxConn = maxConn;

		freeConnections = new Vector<Connection>();
		for (int i = 0; i < initConn; i++) { // 初始normalConn个连接
			Connection conn = createConnection();
			if (conn != null) {
				freeConnections.addElement(conn);
				num++;
			}
		}
	}

	// 释放不用的连接到连接池
	public synchronized void freeConnection(Connection conn) {
		freeConnections.addElement(conn);
		num++;
		activeNum--;
		notifyAll();
	}

	// 获取一个可用连接
	public synchronized Connection getConnection() {
		
		Connection conn = null;
		if (freeConnections.size() > 0) { // 还有空闲的连接
			num--;

			conn = (Connection) freeConnections.firstElement();
			freeConnections.removeElementAt(0);
			try {
				if (conn.isClosed()) {
					System.out.println("从连接池删除一个无效连接");
					conn = getConnection();
				}
			} catch (SQLException e) {
				System.out.println("从连接池删除一个无效连接");
				conn = getConnection();
			}
		} else if (maxConn == 0 || activeNum < maxConn) { // 没有空闲连接且当前连接小于最大允许值,最大值为0则不限制
			conn = createConnection();
		}

		if (conn != null) { // 当前连接数加1
			activeNum++;
		}

		return conn;

	}

	// 获取一个连接,并加上等待时间限制,时间为毫秒
	public synchronized Connection getConnection(long timeout) {
		long startTime = new Date().getTime();
		Connection conn;
		while ((conn = getConnection()) == null) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {	}

			if ((new Date().getTime() - startTime) >= timeout) {
				return null; // 超时返回
			}
		}
		return conn;
	}

	// 关闭所有连接
	public synchronized void release() {
		Enumeration<Connection> allConnections = freeConnections.elements();
		while (allConnections.hasMoreElements()) {
			Connection conn = (Connection) allConnections.nextElement();
			try {
				conn.close();
				num--;
			} catch (SQLException e) {
				System.out.println("无法关闭连接池中的连接");
			}
		}
		freeConnections.removeAllElements();
		activeNum = 0;
	}

	// 创建一个新连接
	private Connection createConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
//			System.out.println("连接池创建一个新的连接");
		} catch (SQLException e) {
//			System.out.println("无法创建这个URL的连接" + url);
		}
		return conn;
	}

	// 返回当前空闲连接数
	public int getNum() {
		return num;
	}

	// 返回当前连接数
	public int getActiveNum() {
		return activeNum;
	}

}
