package crawler.fetcher;

import java.sql.Timestamp;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * 本接口由小鲍提供。
 *
 */
public class HBaseHelper {

	static Configuration cfg = null;
	static HTable preFixedtable= null;
	
	static String preFixedTableName = "ImageCopy";
	static String preFixedColumnFamilyName = "ImageContent";
	static String preFixedColumnName = "content";
	
	static byte[] preFixedcolumnFamilyBytes = Bytes.toBytes(preFixedColumnFamilyName);
	static byte[] preFixedcolumnNameBytes = Bytes.toBytes(preFixedColumnName);
	
	static{
		Configuration HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", "192.168.1.200");
		HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
		if(cfg == null)
			cfg = HBaseConfiguration.create(HBASE_CONFIG);
		try{
			//HBaseHelper.creatTable(preFixedTableName, preFixedColumnFamilyName);
			if(preFixedtable == null)
				preFixedtable = new HTable(cfg, preFixedTableName);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * create new table, print table exist if table's already there. Drop the old one, create a new table
	 * @param tablename table name
	 * @throws Exception
	 */
	public static boolean creatTable(String tablename,String columnFamilyName) throws Exception {
		/*Configuration HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", "192.168.1.200");
		HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
		Configuration newconfig = HBaseConfiguration.create(HBASE_CONFIG);*/
		HBaseAdmin admin = new HBaseAdmin(cfg);

		if (admin.tableExists(tablename)) {
			System.out.println("table   Exists!!!");
			
		} else {
			TableName tn = TableName.valueOf(tablename);
			HTableDescriptor tableDesc = new HTableDescriptor(tn);
			tableDesc.addFamily(new HColumnDescriptor(columnFamilyName));
			admin.createTable(tableDesc);
			System.out.println("create table ok .");
		}
		admin.close();
		return true;
	}
	
	/**
	 * Add picture to fixed table:
	 * Table name: Image
	 * Column family name: ImageContent
	 * Column name: content.   BTW, there is an another column named "oldRowKey" in the table, just leave it alone.
	 * @param imageBytes 
	 * @param ID rowkey
	 * @throws Exception
	 */
	public static void addDataToPrefixedTable(String ID, byte[] imageBytes) throws Exception{
		byte[] row1 = Bytes.toBytes(ID);
		Put p1 = new Put(row1);
		p1.add(preFixedcolumnFamilyBytes, preFixedcolumnNameBytes, imageBytes);
		preFixedtable.put(p1);
		System.out.println("One picture insertted! rowkey(ID): " + ID + ". " + new Timestamp(System.currentTimeMillis()));
	}
	
}

