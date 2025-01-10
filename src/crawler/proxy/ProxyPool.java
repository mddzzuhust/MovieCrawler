package crawler.proxy;

import java.io.IOException;
import java.util.List;
import java.net.Proxy;

/**
 * 代理地址池
 * 代理地址预先写入在文件中,读入程序后再进行测试,有效则加入,无效或响应过慢则丢弃
 * @author liuxue
 */
public class ProxyPool {
	
	private static int index = 0;
	private static int size = 0;
	
	public static List<Proxy> list = null;
	
	public static void parse(String proxyfile) {
		/* 不强制使用代理,没有就不用 */
		if(proxyfile == null || proxyfile.equals(""))
			return;
		ProxyTest test = new ProxyTest();
		try {
			list = test.test(proxyfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		test = null;
		if(list != null) {
			size = list.size();
			for(int i = 0; i < size; i++)
				System.out.println("添加代理地址成功-[" + 
						list.get(i).address().toString().substring(1) + "]");
		}
		
		if(size == 0) {
			System.err.println("没有可用代理");
		}
	}
	
	public static Proxy getProxy() {
		if(size == 0)
			return null;
		index = (index + 1) % size;
		return list.get(index);
	}
	
	public static void main(String[] args) {
		try {
			parse("proxy.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(Proxy proxy : list) {
			System.out.println(proxy.address().toString().substring(1));
		}
	}

}
