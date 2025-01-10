package crawler.util;

import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockConflictException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用Berkeley DB这个小型数据库做为URL仓库
 * URL作为Key，不允许存在相同Key。value值随意
 * @author fugui
 *
 */
public class BDBUtil2{ 
 
   Environment dbEnvironment = null;
   Database db = null;
   Cursor cursor = null;
   static DatabaseEntry fkey = new DatabaseEntry();
   static DatabaseEntry fdata = new DatabaseEntry();
   static boolean pcrs=false;
   //Transaction tran = null;
   
   public BDBUtil2(){
	   openDataBase();
   }
   
   /**
    * 打开默认的数据库
    */
   public boolean openDataBase(){
	   return openDataBase(System.getProperty("user.dir") + "\\cacheDB", "cacheDB");
   }
   
   /**
    * 创建or打开数据库环境和数据库
    * 1.打开数据库环境，不存在则创建；2.打开数据库，不存在则创建
    * @param path 数据库环境路径
    * @param name 数据库名
    * @return
    */
   public boolean openDataBase(String path, String name){	
	   boolean flag = false;
	   try {
		   EnvironmentConfig envConfig = new EnvironmentConfig();
		   envConfig.setAllowCreate(true);//如果不存在则创建一个  
		   envConfig.setReadOnly(false); //true 以只读方式打开，如果是多进程应用，每个进程都要设置为true
		   envConfig.setTransactional(true);//true支持事务，false不支持，默认false。可以更改配置文件来设置
		   dbEnvironment = new Environment(new File(path), envConfig);
		   /*
		   List<String> myDbNames = dbEnvironment.getDatabaseNames(); //得到所有的数据库的名字              
		   for(int i=0; i < myDbNames.size(); i++) {
			   System.out.println("Database Name: " + (String)myDbNames.get(i));
		   }*/
		   
		    // 打开一个数据库，如果数据库不存在则创建一个
		    DatabaseConfig dbConfig = new DatabaseConfig();  
		    dbConfig.setAllowCreate(true);
		    dbConfig.setSortedDuplicates(false); // 设置一个key是否允许存储多个值，true代表允许，默认false
		    dbConfig.setTransactional(true);
		    dbConfig.setReadOnly(false);
		    //dbConfig.setExclusiveCreate(false); // 以独占的方式打开，也就是说同一个时间只能有一实例打开这个database
		    db = dbEnvironment.openDatabase(null,name,dbConfig); //打开一个数据库，数据库名为name
		     

		 } catch (DatabaseException dbe) {
			dbe.printStackTrace();
		}
		return flag;
		
   }
   
   /**
    * 重载 addData,使用默认参数
    * @param Key
    * @return
    */
   public boolean addData(String Key){
       return addData(Key, "");
   }
   
   /**
    * 向数据库中添加数据
    * @param Key
    * @param Data
    * @return
    */
//   public boolean addData(String Key,String Data){
//        boolean flag = false;
//	    
//	    try {
//	        DatabaseEntry theKey = new DatabaseEntry(Key.getBytes("UTF-8"));
//	        DatabaseEntry theData = new DatabaseEntry(Data.getBytes("UTF-8"));
//
//	        
//	        /*
//	        Database.put()
//	               向database中添加一条记录。如果你的database不支持一个key对应多个data或当前database中已经存在该key了，
//	               则使用此方法将使用新的值覆盖旧的值。 
//	               
//	        Database.putNoOverwrite()
//	               向database中添加新值但如果原先已经有了该key，则不覆盖。不管database是否允许支持多重记录(一个key对应多个value),
//	               只要存在该key就不允许添加，并且返回perationStatus.KEYEXIST信息。
//	               
//	        Database.putNoDupData()      
//	               想database中添加一条记录，如果database中已经存在了相同的 key和value则返回 OperationStatus.KEYEXIST
//	        */     
//	        OperationStatus os = db.putNoOverwrite(null, theKey, theData);
//	        if( os == OperationStatus.SUCCESS){
//	        	flag = true;
//	        	//System.out.println("[" + Key + "," + Data + "]" + " insert into success");
//	        } /*else if(os == OperationStatus.KEYEXIST)
//	        	;
//	        	//System.out.println("[" + Key + "," + Data + "]" + ".insert fail. key exist.");
//	        else 
//	        	System.out.println("[" + Key + "," + Data + "]" + " fail insert into database");
//	        */
//	        
//	    } catch (Exception e) {
//	    	e.printStackTrace();
//	    }
//
//	    return flag;
//   }
   public boolean addData(String Key,String Data){
       boolean flag = false;
	    try {
	        DatabaseEntry theKey = new DatabaseEntry(Key.getBytes("UTF-8"));
	        DatabaseEntry theData = new DatabaseEntry(Data.getBytes("UTF-8"));
	        Transaction txc = null;
	        try{
		        TransactionConfig txConfig = new TransactionConfig();
		        txConfig.setSerializableIsolation(true);
		        //txConfig.setReadCommitted(true);
		        txc = dbEnvironment.beginTransaction(null, txConfig);
		
	        /*
	        Database.put()
	               向database中添加一条记录。如果你的database不支持一个key对应多个data或当前database中已经存在该key了，
	               则使用此方法将使用新的值覆盖旧的值。 
	               
	        Database.putNoOverwrite()
	               向database中添加新值但如果原先已经有了该key，则不覆盖。不管database是否允许支持多重记录(一个key对应多个value),
	               只要存在该key就不允许添加，并且返回perationStatus.KEYEXIST信息。
	                
	        Database.putNoDupData()      
	               想database中添加一条记录，如果database中已经存在了相同的 key和value则返回 OperationStatus.KEYEXIST
	        */     
			        OperationStatus os = db.putNoOverwrite(txc, theKey, theData);
			        txc.commit();
			        if( os == OperationStatus.SUCCESS){ 	
			        	flag = true;
			        } 
	        }catch(LockConflictException lockConflict)
	        {
	        	  txc.abort();
	              return false;
	        }

	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return flag;
  }
   
   /**
    * 通过Key值读取记录
    * @param Key
    * @return
    */
//   public boolean findData(String Key){
//	   boolean flag = false;
//	    CursorConfig curConfig = new CursorConfig();
//	    curConfig.setReadUncommittedVoid(true);
//	    
//	    cursor = db.openCursor(null, curConfig); // 打开游标
//	    flag = true;
//	   try {
//		    DatabaseEntry theKey = new DatabaseEntry(Key.getBytes("UTF-8"));
//	        DatabaseEntry theData = new DatabaseEntry();
//	        
//	        OperationStatus retVal = cursor.getSearchKey(theKey,theData, LockMode.DEFAULT);
//	                       //如果count超过一个，则遍历
//
//	        if (cursor.count() == 1 && retVal == OperationStatus.SUCCESS) {
//	        	String dataString = new String(theData.getData(), "UTF-8");
//	        	System.out.println("find key " + "[" + Key + "," + dataString + "]");
//	            flag = true;
//		       
//            } else {
//            	System.out.println("findData error. count > 1");
//            }
//
//	   } catch (Exception e) {
//		    // Exception handling goes here
//			e.printStackTrace();
//		}
//	   cursor.close();
//		return flag;
//   }
   
   /**
    * 获取数据库数据
    * @param numLimit 是否现在获取数量
    * @param num 数量
    * @return  Key集合
    */
//   public List<String> getDatas(boolean numLimit, int num){
//	   List<String> list = null;
//	   int count = 0;
//	   try {
//		    DatabaseEntry theKey = new DatabaseEntry();
//	        DatabaseEntry theData = new DatabaseEntry();
//	        list = new ArrayList<String>();
//	        
//	        while(cursor.getNext(theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS){
//	        	String keyString = new String(theKey.getData(), "UTF-8");
//    	    	//String dataString = new String(theData.getData(), "UTF-8");
//    	    	list.add(keyString);
//    	    	++count;
//    	    	if(numLimit && count > num)
//    	    		break;
//    	    	//System.out.println("");
//	        }
//	        
//	   } catch (Exception e) {
//		    // Exception handling goes here
//			e.printStackTrace();
//		}
//		return list;
//   }
   public List<String> getDatas(boolean numLimit, int num){
	   List<String> list = null;
	   int count = 0;
	   Transaction txn = null;
	   Cursor myCursor = null;
	   try {
		   
	        list = new ArrayList<String>();
	       
	       // TransactionConfig txConfig = new TransactionConfig();
	       // txConfig.setSerializableIsolation(true);
		    txn = dbEnvironment.beginTransaction(null, null);
	        CursorConfig cc = new CursorConfig();
	        
	        cc.setReadCommitted(true);
	        myCursor = db.openCursor(txn, cc);
	        DatabaseEntry foundKey = fkey;
	        DatabaseEntry foundData = fdata;

	        if(pcrs)
	        myCursor.getSearchKey(foundKey, foundData, LockMode.DEFAULT);
	        while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT)  == OperationStatus.SUCCESS) 
	        {
	        	String theKey = new String(foundKey.getData(), "UTF-8");
	                String theData = new String(foundData.getData(), "UTF-8");
	        	list.add(theKey);
	        	++count;
	           	if(numLimit && count > num)
	           	    break;
	        }
	        fkey=foundKey;
	    	fdata=foundData;
	    	pcrs=true;
	        myCursor.close();	
	        txn.commit(); 
	   } catch (Exception e) {
		    // Exception handling goes here
			e.printStackTrace();
		}
		return list;
   }
   
//   /**
//    * 通过key值删除记录
//    * @param Key
//    */
//   public void deleteData(String Key){
//	   try{
//		   DatabaseEntry theKey = new DatabaseEntry(Key.getBytes("UTF-8"));
//		   OperationStatus result = db.delete(null, theKey);
//		   if(result == OperationStatus.SUCCESS)
//			   System.out.println("[" + Key + "]" + " delete");
//		   else
//			   System.out.println("fail to delete " + "[" + Key + "]");
//	    } catch (Exception e) {
//	    	e.printStackTrace();
//	   }
//   }
   
   /**
    * 通过key，data修改记录（暂时没改动数据库数据的需求）
    * @param Key
    * @param Data
    * @param ReplaceData
    * @return
    */
//   public boolean alterData(String Key,String Data,String ReplaceData){
//	   boolean flag = false;
//	    CursorConfig curConfig = new CursorConfig();
//	    curConfig.setReadUncommittedVoid(true);
//	    
//	    cursor = db.openCursor(null, curConfig); // 打开游标
//	    flag = true;
//	   try{
//		   DatabaseEntry theKey = new DatabaseEntry(Key.getBytes("UTF-8"));
//		   DatabaseEntry theData = new DatabaseEntry(Data.getBytes("UTF-8"));
//		   OperationStatus retVal = cursor.getSearchBothRange(theKey,theData,LockMode.DEFAULT);    
//      
//		   //将要被替换的值
//	       if (retVal == OperationStatus.SUCCESS) {
//	    	   DatabaseEntry replacementData = new DatabaseEntry(ReplaceData.getBytes("UTF-8"));
//	    	   cursor.delete();
//	           cursor.put(theKey,replacementData);//把当前位置用新值替换 
//	           flag = true;
//	       }
//	       else
//	    	System.out.println("没有要修改的值");
//		   
//	   }catch(Exception e){
//		   e.printStackTrace();
//	   }
//	   cursor.close();
//	   return flag;
//   }
   
   /**
    * 关闭数据库和数据库环境
    */
   public  void closeDatabase(){
       try {
    	   if (cursor != null) {   
                cursor.close();
                System.out.println("close cursor");
           }
           if (db != null) {
               db.close();
               System.out.println("close database");
           }
           if (dbEnvironment != null) {
        	//   myDbEnvironment.cleanLog(); // 在关闭环境前清理下日志
               dbEnvironment.close();
               System.out.println("close db environment");
           }
       
       } catch (DatabaseException dbe) {
    	   dbe.printStackTrace();
       }
   }
   
   public static void main(String[] args){
	   BDBUtil db = new BDBUtil();
	   //System.out.println(db.getDatas().size());
	   //db.findData("test");
	   db.addData("zhang","");
	   db.addData("hello1", "");
	   db.addData("test", "");  
	   db.addData("hello2", "");
	   db.addData("hello3", "");
	   
	   //db.addData("test", "just test");
	   List<String> list1 =db.getDatas(true,1);
	   
	   for(int i=0;i<list1.size();i++)
	   {
		   System.out.println(list1.get(i));
	   }
	   List<String> list2 =db.getDatas(true,0);
	   
	   for(int i=0;i<list2.size();i++)
	   {
		   System.out.println(list2.get(i));
	   }

	  db.closeDatabase();
   }
}













