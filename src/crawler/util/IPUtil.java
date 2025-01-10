package crawler.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
//import java.net.UnknownHostException;
import java.util.Enumeration;

public class IPUtil {

	/**
	 * 获取本机IP地址. 在windows和linux上均可正确运行
	 * @return 本机IP地址,非"127.0.0.1"形式
	 * @throws IOException
	 */
	public static String getLocalAddress() {
		/* 方法一
		 * 直接获取本地IP地址. 在Linux下为127.0.0.1(没测试过)
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(addr.getHostAddress());*/
		
		/* 方法二
		 * 本方法会获取本机所有网卡的所有IP地址,因此需要进行过滤
		 * 在电信网络下,IP均为192168.1.X。本方法中isSiteLocalAddress()为true.
		 * 无法获取地址IP地址,返回null.故注释掉
		 * 在实验室的校园网中,可正常使用,无需注释.
		 */
    	Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}
    	Enumeration<InetAddress> inetAddress;
    	InetAddress ip = null;
    	while(netInterfaces.hasMoreElements()) {
    		inetAddress = netInterfaces.nextElement().getInetAddresses();
    		while(inetAddress.hasMoreElements()) {
    			ip = inetAddress.nextElement(); 
    			/* 过滤IP地址
    			 * isSiteLocalAddress() 判断是否为站点本地地址(192168.1.X为true). 电信网络下注释掉
    			 * isLoopbackAddress() 判断是否为本地回环测试地址(127.0.0.1为true)
    			 * ip.getHostAddress().indexOf(":") 区分IPv4(如192.168.1.103) 与 IPv6(如fe80:0:0:0:0:100:7f:fffe%11)
    			 */
    			if(/*!ip.isSiteLocalAddress() &&*/ !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
    				return ip.getHostAddress();
    			}
    		}
    	}
    	return null;
    }
}
