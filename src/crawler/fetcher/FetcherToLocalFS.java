package crawler.fetcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import crawler.client.CrawlerClient;
import crawler.config.Config;
import crawler.feeder.Task;
import crawler.util.FileFactory;

/**
 * 下载图像文件到本地文件
 * @author fugui
 */
public class FetcherToLocalFS implements IFetcher{

	public static Config config = Config.getInstance();
	public final static int MAX_IMAGE_SIZE = config.getMaxImageSize();
	public final static int MIN_IMAGE_SIZE = config.getMinImageSize();
	
	private FileOutputStream fos = null;
	
	public FetcherToLocalFS() {
	}
	
	@Override
	public String fetch(String imgName, byte[] imgBytes) throws Exception {
				
		//图像保存文件。此时imgName为图像后缀名，文件名自动生成。
        File file = FileFactory.createImageFile(imgName); 
        if(file == null) {
        	return null;
        }
        
		try {
			fos = new FileOutputStream(file);
			fos.write(imgBytes);    // 一次性写出所有数据
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally{
			if(fos != null)
				fos.close();
			
			long fileSize = file.length();
			if (fileSize <= MIN_IMAGE_SIZE || (MAX_IMAGE_SIZE != -1 && fileSize > MAX_IMAGE_SIZE)) {
        		if(CrawlerClient.debug)
            		Task.logger.info("文件大小不合要求丢弃. " + fileSize);
        		file.delete();
        		System.out.println("ImageFetcher. 文件大小不合要求丢弃. " + fileSize);
        		return null; //文件丢弃了.但仍返回true,表示该地址被成功抓取
        	}
		}

		return file.getAbsolutePath();
	}
}
