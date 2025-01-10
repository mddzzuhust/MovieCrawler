package crawler.fetcher;

/**
 * 下载图像。保存在本地文件系统或者Hbase中。
 */
public interface IFetcher {
	
	/**
	 * 
	 * @param imgName 文件名
	 * @param imgBytes 文件内容。字节流。
	 * @return String 文件保存路径
	 * @throws Exception
	 */
	public String fetch(String imgName, byte[] imgBytes)  throws Exception;

}
