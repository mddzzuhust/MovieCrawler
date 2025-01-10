package crawler.fetcher;

/**
 * 下载的文件保存到HBase中。
 * @author fugui
 *
 */
public class FetcherToHbase implements IFetcher{

	@Override
	public String fetch(String imgName, byte[] imgBytes) throws Exception {
		// 直接调用对应的接口。由小鲍提供
		HBaseHelper.addDataToPrefixedTable(imgName ,imgBytes);
		return null;
	}
}
