package crawler.mysql;

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
    	connectStr = "jdbc:mysql://" + config.getDbip() + ":3307/" + config.getDbsid();
    }
    
    public DBConn(String dbip,String dbsid,String dbuser,String dbpw) {
    	this.databaseip = dbip;
    	this.databasesid = dbsid;
    	this.databaseuser = dbuser;
    	this.databasepasswd = dbpw;
    	connectStr = "jdbc:mysql://" + databaseip + ":3307/" + databasesid;
    }
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
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
    
    public static void main(String []args) 
    {
    	DBConn dn=new DBConn("192.168.1.230","idcmedia","root","123456");
    	Connection conn=dn.getConnection();
    	try {
    		String sql="select * from movie";
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sql);			
			while(rs.next()) {

		          // 输出结果
		          System.out.println(rs.getString("title"));
			}

		         rs.close();
		         conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
       
    }

}