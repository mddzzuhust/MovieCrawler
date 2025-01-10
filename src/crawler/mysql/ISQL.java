package crawler.mysql;

import java.sql.SQLException;

public interface ISQL {
	
	public int getTotalNum() throws SQLException;
	
	public boolean executeSQL(String sqlCondition) throws SQLException;

}
