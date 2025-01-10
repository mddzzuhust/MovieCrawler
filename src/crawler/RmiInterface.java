package crawler;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("all")
public interface RmiInterface extends Remote {
	
	/**
	 * 获得所有爬虫客户端的信息(ID号,IP地址,所拥有的哈希号)
	 * @return 所有爬虫主机的信息
	 * @throws RemoteException
	 */
	HashMap getClients() throws RemoteException;
	
	/**
	 * 获得对应哈希号已爬行URL判重信息
	 * @return URL判重bitset结构
	 * @throws RemoteException
	 */
	BitSet getCrawledURL() throws RemoteException;
	
	/**
	 * 爬虫向服务器上传自己的已爬行URL列表
	 * @param updateSet URL的check值集合
	 * @param crawls 已爬行URL个数
	 * @param errors 错误的URL个数
	 * @param crawlerid 爬虫ID
	 * @throws RemoteException
	 */
	void updateCrawledURL(HashSet<Integer> updateSet, int crawls, int errors, int crawlerid) throws RemoteException;
	
	/**
	 * 爬虫获取服务器设定的初始URL
	 * @param hashCode
	 * @return
	 * @throws RemoteException
	 */
	ArrayList<String> getInitURL(ArrayList hashCode) throws RemoteException;
	
	/**
	 * 获取初始哈希值个数
	 */
	int getTotalHashNum() throws RemoteException;
	

}
