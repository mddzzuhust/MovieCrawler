package crawler.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import crawler.BloomFilter;
import crawler.client.Client;
import crawler.client.CrawlerClient;
import crawler.feeder.Task;
import crawler.url.URLUtil;
import crawler.util.BDBUtil;

/**
 * 拟打算用BerkeleyDB这个小型数据库做为URL仓库.尚未实现. @author liuxue
 * 
 * Berkeley DB的相关操作在包crawler.util中的BDBUtil.java中已经实现
 * @author fugui
 */
public class BDBURLProvider implements IURLProvider {

	private int savedUrlsToCrawlSize = 0;
	private int savedUrlsErrorSize = 0;
	private int savedUrlsToDispatchSize = 0;
	private int savedUrlsCrawledSize = 0;
	private int savedVideoUrlsToCrawlSize = 0;
	
	//private int toReadUrlFileIndex = 1;
	//private int toWriteUrlFileIndex = 1;
	private int toReadDisFileIndex = 1;
	private int toWriteDisFileIndex = 1;
	private int toReadVideoUrlFileIndex = 1;
	private int toWriteVideoUrlFileIndex = 1;
	
	private String separator = System.getProperty("line.separator");
	
	BDBUtil bdb = new BDBUtil();
	
	public void saveUrlStatus() {
		Properties prop = new Properties();
		//prop.setProperty("toReadUrlFileIndex", String.valueOf(toReadUrlFileIndex));
		//prop.setProperty("toWriteUrlFileIndex", String.valueOf(toWriteUrlFileIndex));
		prop.setProperty("toReadDisFileIndex", String.valueOf(toReadDisFileIndex));
		prop.setProperty("toWriteDisFileIndex", String.valueOf(toWriteDisFileIndex));
		prop.setProperty("toReadVideoUrlFileIndex", String.valueOf(toReadVideoUrlFileIndex));
		prop.setProperty("toWriteVideoUrlFileIndex", String.valueOf(toWriteVideoUrlFileIndex));
		
		prop.setProperty("savedUrlsToCrawlSize", String.valueOf(savedUrlsToCrawlSize));
		prop.setProperty("savedUrlsToDispatchSize", String.valueOf(savedUrlsToDispatchSize));
		prop.setProperty("savedVideoUrlsToCrawlSize", String.valueOf(savedVideoUrlsToCrawlSize));
		java.io.OutputStream out = null;
		try {
			out = new java.io.FileOutputStream("status.properties");
			prop.store(out, "urls file's info for increment crawl");
		} catch (IOException e) {
			System.err.println("FileURLProvider保存配置失败" + e.getMessage());
		} finally {
			if(null != out)
			try {
				out.close();
			} catch(Exception e) {
			}
		}
	}

	public void loadUrlStatus() {
		FileReader reader = null;
		Properties prop = new Properties();
		try {
			File file = new File("status.properties");
			if(file.exists() && file.canRead()) {
				reader = new FileReader(file);
				prop.load(reader);
				//toReadUrlFileIndex = Integer.parseInt(prop.getProperty("toReadUrlFileIndex", "1"));
				//toWriteUrlFileIndex = Integer.parseInt(prop.getProperty("toWriteUrlFileIndex", "1"));
				toReadDisFileIndex = Integer.parseInt(prop.getProperty("toReadDisFileIndex", "1"));
				toWriteDisFileIndex = Integer.parseInt(prop.getProperty("toWriteDisFileIndex", "1"));
				toReadVideoUrlFileIndex = Integer.parseInt(prop.getProperty("toReadVideoUrlFileIndex", "1"));
				toWriteVideoUrlFileIndex = Integer.parseInt(prop.getProperty("toWriteVideoUrlFileIndex", "1"));
				
				savedUrlsToCrawlSize = Integer.parseInt(prop.getProperty("savedUrlsToCrawlSize", "0"));
				savedUrlsToDispatchSize = Integer.parseInt(prop.getProperty("savedUrlsToDispatchSize", "0"));
				savedVideoUrlsToCrawlSize = Integer.parseInt(prop.getProperty("savedVideoUrlsToCrawlSize", "0"));
			}
		} catch(Exception e) {
			System.err.println("加载状态配置失败,无法继续上次爬行");
			//toReadUrlFileIndex = 1;
			//toWriteUrlFileIndex = 1;
			toReadDisFileIndex = 1;
			toWriteDisFileIndex = 1;
			toReadVideoUrlFileIndex = 1;
			toWriteVideoUrlFileIndex = 1;
		} finally {
			try {
				if(reader != null)
				reader.close();
			} catch (IOException e) {
			}
		}
		
	}
	
	public int getSavedUrlsCrawledSize() {
		return savedUrlsCrawledSize;
	}

	public int getSavedUrlsErrorSize() {
		return savedUrlsErrorSize;
	}

	public int getSavedUrlsToCrawlSize() {
		return savedUrlsToCrawlSize;
	}

	public int getSavedUrlsToDispatchSize() {
		return savedUrlsToDispatchSize;
	}
	
	public int loadToCrawlUrls() {
		int loadsize = 0;
		List<String>  list = new ArrayList<String>();
		try {	
			synchronized(Task.urlsToCrawl) {
				list = bdb.getDatas(true, IURLProvider.savePerSize);
				if (Task.urlsToCrawl.addAll(list)){
					System.out.println("成功读取数据库数据.BDBURLProvider");
					loadsize += list.size();
				}
			}
			savedUrlsToCrawlSize -= loadsize;
			System.out.println("load urls -[ " + loadsize + " ]- from BDB");
		} catch(Exception e) {
			Task.logger.warning("从文件读取待爬行URL列表失败!" + separator + e.getMessage());
		}
		return loadsize;
	}

	public int loadToDispatchUrls() {
		if(toReadDisFileIndex == toWriteDisFileIndex)
			return 0;
		
		String url;
		int loadsize = 0;
		File file = null;
		BufferedReader reader = null;
		try {
			File dir = new File(Client.config.getImageSavePath() + "/urlsToDispatch");
			if (!dir.exists()) {
				return 0;
			}
			
			file = new File(dir+"/urls" + toReadDisFileIndex + ".txt");
			reader = new BufferedReader(new FileReader(file));
			while ((url = reader.readLine()) != null) {
				if(!url.startsWith("http"))
					continue;
				Task.urlsToDispatch.get(URLUtil.getHashcode(url)).add(url);
				loadsize++;
			}
			savedUrlsToDispatchSize -= loadsize;
		} catch(Exception e) {
			if (file != null)
				file.delete();
			Task.logger.warning("从文件读取待分发URL列表失败!" + separator + e.getMessage());
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch(IOException e1) {		}
			if (file != null)  //不管读取是否发生异常,读完均删除
				file.delete();
			toReadDisFileIndex++;  //不会重复读取同一文件
		}
		return loadsize;
	}

	@Override
	public int loadToTestUrls() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void saveCrawledUrls() {
		if(Task.getCrawledUrlSize() == 0)
			return;
		
		Task.updateSet.clear();
		FileWriter writer = null;
		String url = null;
		int total = 0;
		int num = 0;
		try {
			File dir = new File(Client.config.getImageSavePath() + "/urlCrawled");
			if (!dir.exists()) {
				dir.mkdirs();
			} else {
				File files[] = dir.listFiles();
				num = files.length;
				files = null;
			}
			
			writer = new FileWriter(new File(dir + "/urls" + (num+1) + ".txt"));
			for (int i = 0, m = Task.urlsCrawled.size(); i < m; i++) {
				for (int j = 0, n = Task.urlsCrawled.get(i).size(); j < n; j++) {
					url = Task.urlsCrawled.get(i).poll();
					if (url != null) {
						writer.write(url + separator);
						Task.updateSet.add(BloomFilter.hash1(url));
						Task.updateSet.add(BloomFilter.hash2(url));
						Task.updateSet.add(BloomFilter.hash3(url));
						total++;
					}
				}
			}
			
			savedUrlsCrawledSize += total;
			String endLine = "Total: " + total + "	" + " Time: " + new Date().toString();
			writer.write(endLine);
			writer.flush();
			writer.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		try {
			if (Task.updateSet.size() != 0) {
				int errors = savedUrlsErrorSize + Task.urlsError.size() - Task.updatedErrorUrls;
				CrawlerClient.updateCrawledURL(total, errors);
				Task.updatedErrorUrls += errors;
			}
		} catch (RemoteException e) {
			System.out.println("上传已爬行URL队列失败!");
			e.printStackTrace();
		}

	}

	public void saveErrorUrls() {
		if(Task.urlsError.isEmpty())
			return;
		
		FileWriter writer = null;
		int num = 0;
		try {
			File dir = new File(Client.config.getImageSavePath() + "/errorUrls");
			if(!dir.exists()) {
				dir.mkdirs();
			}
			else{
				File files[] = dir.listFiles();
				num = files.length;
				files = null;
			}
			
			String url;
			writer = new FileWriter(new File(dir + "/urls" + (num+1) + ".txt"));
			int savesize = 0;
			for(int i = 0, n = Task.urlsError.size(); i < n; i++) {
				url = Task.urlsError.poll();
				if(url != null) {
					writer.write(url+ separator);
					savesize++;
				}
			}
			
			String endLine = "Total: " + savesize + " Time: " + new Date().toString();
			writer.write(endLine);
			writer.flush();
			writer.close();
			savedUrlsErrorSize += savesize;
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	
	public void saveToCrawlUrls() {
		try {
			String url;
			int savesize = 0;
			
			synchronized (Task.urlsToCache) {
				for (int i = 0; i < savePerSize; i++) {
					url = Task.urlsToCache.poll();
					if(url != null) {
						bdb.addData(url);
						savesize++;
					}
				}
			}

			savedUrlsToCrawlSize += savesize;
			//System.out.println("save urlsToCrawl -[ " + savesize + " ]- into BDB.");
		} catch(Exception e) {
			Task.logger.warning("保存待爬行URL列表至文件失败!" + separator + e.getMessage());
		} 
	}

	public void saveToDispatchUrls() {
		File file = null;
		FileWriter writer = null;
		try {
			File dir = new File(Client.config.getImageSavePath() + "/urlsToDispatch");
			if(!dir.exists()){
				dir.mkdir();
			}
				
			String url;
			int savesize = 0;
			file = new File(dir + "/urls" + toWriteDisFileIndex + ".txt");
			writer = new FileWriter(file);
			for (int i = 0; i < savePerSize; i++) {
				for (int j = 0, m = Task.urlsToDispatch.size(); j < m; j++) {
					for (int k = 0, n = Task.urlsToDispatch.get(j).size(); k < n; k++) {
						if ((url = Task.urlsToDispatch.get(j).poll()) != null) {
							writer.write(url + separator);
							savesize++;
						}
					}
				}
			}
			String endLine = "Total: " + savesize + "	" + " Time: " + new Date().toString();
			writer.write(endLine);
			writer.flush();
			
			toWriteDisFileIndex++;
			savedUrlsToDispatchSize += savesize;
		} catch(Exception e) {
			if (file != null)
				file.delete();
			Task.logger.warning("保存待分发URL列表至文件失败!" + separator + e.getMessage());
		} finally {
			if (writer != null)
			try {
				writer.close();
			} catch(IOException e1) {		}
		}
	}

	@Override
	public void saveToTestUrls() {
		// TODO Auto-generated method stub

	}
	
	public void closeBDB(){
		if(bdb != null)
			bdb.closeDatabase();
	}
	
	public static void main(String[] args){
		BDBURLProvider test = new BDBURLProvider();
		test.loadToCrawlUrls();
	}

}
