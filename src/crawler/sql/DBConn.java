package crawler.sql;

import java.sql.*;

import crawler.config.Config;


public class DBConn {

    private String databaseip;
    private String databasesid;
    private String databaseuser;
    private String databasepasswd;
    private Connection conn;
    private String connectStr;
    private Config config = Config.getInstance();
    
    public DBConn() {
    	this.databaseip = config.getDbip();
    	this.databasesid = config.getDbsid();
    	this.databaseuser = config.getDbuser();
    	this.databasepasswd = config.getDbpw();
    	connectStr = "jdbc:oracle:thin:@" + config.getDbip() + ":1521:" + config.getDbsid();
    }
    
    public DBConn(String dbip,String dbsid,String dbuser,String dbpw) {
    	this.databaseip = dbip;
    	this.databasesid = dbsid;
    	this.databaseuser = dbuser;
    	this.databasepasswd = dbpw;
    	connectStr = "jdbc:oracle:thin:@" + databaseip + ":1521:" + databasesid;
    }

    public Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(connectStr, databaseuser, databasepasswd);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void closeDB() {
        if(conn != null)
            try {
                conn.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
    }

}