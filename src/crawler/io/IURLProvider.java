package crawler.io;

public interface IURLProvider {

	public static int savePerSize = 20000;
	
	/**
	 * 获得/保存待爬行URL
	 */
	public int loadToCrawlUrls();
	public void saveToCrawlUrls();
	
	/**
	 * 获得/保存待分发URL
	 */
	public int loadToDispatchUrls();
	public void saveToDispatchUrls();
	
	/**
	 * 获得/保存待判重及过滤的URL
	 * @return
	 */
	public int loadToTestUrls();
	public void saveToTestUrls();
	
	/**
	 * 保存已下载URL
	 */
	public void saveCrawledUrls();
	
	/**
	 * 保存爬行失败的URL
	 */
	public void saveErrorUrls();
	
	/**
	 * 获得/保存URL处理状态
	 */
	public void loadUrlStatus();
	public void saveUrlStatus();
	
	
	public int getSavedUrlsToCrawlSize();

	public int getSavedUrlsErrorSize();

	public int getSavedUrlsToDispatchSize();

	public int getSavedUrlsCrawledSize();

}
