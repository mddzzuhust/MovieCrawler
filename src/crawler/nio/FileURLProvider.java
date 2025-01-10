package crawler.nio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import crawler.BloomFilter;
import crawler.client.Client;
import crawler.client.CrawlerClient;
import crawler.feeder.Task;
import crawler.url.URLUtil;

public class FileURLProvider implements IURLProvider {

	private int savedUrlsToCrawlSize = 0;
	private int savedUrlsToDispatchSize = 0;
	
	private int savedUrlsErrorSize = 0;
	private int savedUrlsCrawledSize = 0;
	
	private int toReadUrlFileIndex = 1;
	private int toWriteUrlFileIndex = 1;
	
	private int toReadDisFileIndex = 1;
	private int toWriteDisFileIndex = 1;
	
	private String separator = System.getProperty("line.separator");
	
	public FileURLProvider() {
		if(crawler.config.Config.getInstance().isIncrement())
			loadUrlStatus();
	}
	
	@Override
	public int loadToCrawlUrls() {
		/*if (toReadUrlFileIndex == toWriteUrlFileIndex)
			return 0;*/

	    //System.out.println(new Timestamp(System.currentTimeMillis()));
	    
		String url = null;
		int loadsize = 0;
		File file = null;
		//BufferedReader reader = null;
		FileInputStream fin = null;
		FileChannel fcin = null;
		try {/*
			File dir = new File(Client.config.getImageSavePath() + "/tempUrlsToCrawl");
			if(!dir.exists()){
				return 0;
			}*/
			
			//file = new File(dir + "/urls" + toReadUrlFileIndex + ".txt");
			//reader = new BufferedReader(new FileReader(file));
			fin = new FileInputStream("D:\\CrawlerDownload\\urlCrawled\\urls2.txt");
			fcin = fin.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate( 1024 );
			//CharBuffer buffer = CharBuffer.allocate(1024);
			/*while (true) {
				 buffer.clear();
			      if ((fcin.read(buffer)) == -1) {
			        break;
			      }      
			      byte[] b = buffer.array();
			      System.out.print(new String(b));*/
			      /*if (!url.startsWith("http"))
						continue;
			      if (Task.urlsToCrawl.add(url))
						loadsize++;*/
			//}
			
			while (fcin.read(buffer) != -1) {
			   int size = buffer.position();
			   buffer.rewind(); // 将position置0
			   byte[] bs = new byte[1024];
			   buffer.get(bs); // 把文件当字符串处理，直接打印做为一个例子。
			   System.out.print(new String(bs, 0, size));
			   buffer.clear();
		  }
			 
			/*
			synchronized(Task.urlsToCrawl) {
				while ((url = reader.readLine()) != null) {
					if (!url.startsWith("http"))
						continue;
					if (Task.urlsToCrawl.add(url))
						loadsize++;
				}
			}*/
			//savedUrlsToCrawlSize -= loadsize;
			//System.out.println("load urls -[ " + loadsize + " ]- from file: " + file.getAbsolutePath());
		} catch(Exception e) {
			Task.logger.warning("从文件读取待爬行URL列表失败!" + separator + e.getMessage());
		} finally {
			if(fcin != null)
				try {
					fcin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(fin != null)
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			/*
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e1) {		}*/
			if (file != null)  //读取完后,将该文件删除
				file.delete();
			//toReadUrlFileIndex++;  //不管读取是否发生异常,该值均加1,下次直接读取下一文件
		}
		return loadsize;
	}
	@Override
	public void saveToCrawlUrls() {
		File file = null;
		FileWriter writer = null;
		try {
			File dir = new File(Client.config.getImageSavePath() + "/tempUrlsToCrawl");
			if(!dir.exists()){
				dir.mkdirs();
			}
			
			String url;
			int savesize = 0;
			file = new File(dir + "/urls" + toWriteUrlFileIndex + ".txt");
			writer = new FileWriter(file);
			synchronized (/*Task.urlsToCrawl*/ Task.urlsToCache) {
				for (int i = 0; i < savePerSize; i++) {
					url = /*Task.urlsToCrawl.poll()*/ Task.urlsToCache.poll();
					if(url != null) {
						writer.write(url + separator);
						savesize++;
					}
				}
			}
			String endLine = "Total: " + savesize + "	" + " Time: " + new Date().toString();
			writer.write(endLine);
			writer.flush();
			
			toWriteUrlFileIndex++;
			savedUrlsToCrawlSize += savesize;
			System.out.println("save urlsToCrawl -[ " + savesize + " ]- from file: " + file.getAbsolutePath());
		} catch(Exception e) {
			if (file != null)  //若写入失败,则将该文件删除,下次重新写入
				file.delete();
			Task.logger.warning("保存待爬行URL列表至文件失败!" + separator + e.getMessage());
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e1) {		}
		}
	}
	
	@Override
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
	public int loadToTestUrls() {
		return 0;
	}
	@Override
	public void saveToTestUrls() {
		
	}
	
	@Override
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
	
	@Override
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
	
	public void loadUrlStatus() {
		FileReader reader = null;
		Properties prop = new Properties();
		try {
			File file = new File("status.properties");
			if(file.exists() && file.canRead()) {
				reader = new FileReader(file);
				prop.load(reader);
				toReadUrlFileIndex = Integer.parseInt(prop.getProperty("toReadUrlFileIndex", "1"));
				toWriteUrlFileIndex = Integer.parseInt(prop.getProperty("toWriteUrlFileIndex", "1"));
				toReadDisFileIndex = Integer.parseInt(prop.getProperty("toReadDisFileIndex", "1"));
				toWriteDisFileIndex = Integer.parseInt(prop.getProperty("toWriteDisFileIndex", "1"));
				
				savedUrlsToCrawlSize = Integer.parseInt(prop.getProperty("savedUrlsToCrawlSize", "0"));
				savedUrlsToDispatchSize = Integer.parseInt(prop.getProperty("savedUrlsToDispatchSize", "0"));
			}
		} catch(Exception e) {
			System.err.println("加载状态配置失败,无法继续上次爬行");
			toReadUrlFileIndex = 1;
			toWriteUrlFileIndex = 1;
			toReadDisFileIndex = 1;
			toWriteDisFileIndex = 1;
		} finally {
			try {
				if(reader != null)
				reader.close();
			} catch (IOException e) {
			}
		}
	}
	public void saveUrlStatus() {
		Properties prop = new Properties();
		prop.setProperty("toReadUrlFileIndex", String.valueOf(toReadUrlFileIndex));
		prop.setProperty("toWriteUrlFileIndex", String.valueOf(toWriteUrlFileIndex));
		prop.setProperty("toReadDisFileIndex", String.valueOf(toReadDisFileIndex));
		prop.setProperty("toWriteDisFileIndex", String.valueOf(toWriteDisFileIndex));
		
		prop.setProperty("savedUrlsToCrawlSize", String.valueOf(savedUrlsToCrawlSize));
		prop.setProperty("savedUrlsToDispatchSize", String.valueOf(savedUrlsToDispatchSize));
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

	public int getSavedUrlsToCrawlSize() {
		return savedUrlsToCrawlSize;
	}

	public int getSavedUrlsErrorSize() {
		return savedUrlsErrorSize;
	}

	public int getSavedUrlsToDispatchSize() {
		return savedUrlsToDispatchSize;
	}

	public int getSavedUrlsCrawledSize() {
		return savedUrlsCrawledSize;
	}
	
	public static void main(String[] args){
		FileURLProvider test = new FileURLProvider();
		test.loadToCrawlUrls();
	}
}
