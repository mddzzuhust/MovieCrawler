package crawler.io;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

import crawler.crawler.Crawler;
import crawler.util.ZipUtil;

public class PageSaver {
	
	public static AtomicInteger saveIndex = new AtomicInteger(0);
	public static Map<String, List<AtomicInteger>> saveMap = new ConcurrentHashMap<String, List<AtomicInteger>>();
	
	private FileWriter writer = null;
	private final static int MaxfileNum = 999;
	private final static String file_separator = System.getProperty("file.separator");
	
	public boolean save(String url, String content) {
		File file = null;
		String path = null;
		String ext = null;
		int pos = url.lastIndexOf('.');
		if(pos == -1 || url.length() - pos > 6) {
			ext = ".html";
		} else {
			ext = url.substring(pos);
		}
		boolean done = false;
		try {
			path = getSavePath();
			saveMap.get(path).get(1).incrementAndGet();
			file = new File(path + file_separator + System.currentTimeMillis() + ext);
			writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			done = true;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e) {
			}
			if(!done && file != null && file.exists()) {
				file.delete();
			}
			int total1 = saveMap.get(path).get(0).incrementAndGet();
			int total2 = saveMap.get(path).get(1).decrementAndGet();
			if(total2 == 0 && total1 >= MaxfileNum + 1) {
				zipFile(path);
				synchronized(saveMap) {
					saveMap.remove(path);
				}
			}
		}
		return done;
	}

	private String getSavePath() throws Exception {
		String format = "page";
		String path = Crawler.rootPath + file_separator + format;
        
		File root = new File(path);
		if(!root.exists())
			root.mkdir();
        
		int i = saveIndex.get();
		String currentPath = null;
		File currentDirection = null;
		if (i == 0) {
			currentPath = path + file_separator + format + 1;
			currentDirection = new File(currentPath);
			currentDirection.mkdir();
			saveIndex.incrementAndGet();
		} else {
			currentPath = path + file_separator + format + i;
			currentDirection = new File(currentPath);
		}

        String savePath = currentDirection.getAbsolutePath();
        synchronized(saveMap) {
        	 if(!saveMap.containsKey(savePath)) {
        		 List<AtomicInteger> list = new ArrayList<AtomicInteger>(2);
        		 list.add(new AtomicInteger(0));
        		 list.add(new AtomicInteger(0));
        		 saveMap.put(savePath, list);
             }
        }
       if (saveMap.get(savePath).get(0).intValue() >= MaxfileNum) {
			int index = saveIndex.get();
			currentPath = path + file_separator + format + (index + 1);
			currentDirection = new File(currentPath);
			currentDirection.mkdir();
			saveIndex.compareAndSet(index, index + 1);
		}
        
		return savePath;
	}
	
	private void zipFile(String path) {
		ZipUtil zipUtil = new ZipUtil();
		try {
			zipUtil.zipFile(path, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		PageSaver saver = new PageSaver();
		saver.save("http://www.baidu.com, 1", "baidu yixia, ni jiu zhidao");
		System.out.println("sfsd");
	}
}
