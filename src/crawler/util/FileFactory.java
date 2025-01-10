package crawler.util;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import crawler.config.Config;
import crawler.crawler.Crawler;

/**
 * 生成文件,返回给线程用于保存爬取的内容
 * 有一个事实,windows上文件名(目录加名字)长度不能大于256(可等于,并且不算盘符如F:\)
 * linux(red hat上测试过)上文件名长度不能大于255,路径名长度不能大于292.
 * @author liuxue
 *
 */
public class FileFactory {
	
	public static Config config = Config.getInstance();
	// 单个文件夹里存放的最多文件个数
    public final static int MAX_FILES_PER_DIR = config.getMaxFilesPerDir();
//    public final static int MAX_FILES_PER_DIR = 1;
	private final static String file_separator = System.getProperty("file.separator");
	private final static String rootPath = Crawler.rootPath;
	
	public static File createFile(String type, String format) {
		return createFile(type, format, null);
	}
	
	/**
	 * 获取一个文件,用于保存下载的内容
	 * @param type 文件类型
	 *             视频-Type.Video
	 *             音频-Type.Audio
	 *             图像-Type.Image
	 *             文本-Type.Page
	 *             未知-Type.Unknown
	 * @param format 文件格式
	 * @return 一个可写文件
	 */
	public static File createFile(String type, String format, String name) {
		String path = rootPath + file_separator + type + file_separator	+ format;
        String currentPath = null;
        File currentDirection = null;
        
        //若子文件夹为空,则新建,否则寻找号码最大的文件夹,即最近的写入文件夹
        File root = new File(path);
        if(!root.exists())
        	root.mkdirs();
 
//        int i = root.list().length;
//        if (i == 0) {
//            currentPath = path + file_separator + format + 1;
//            currentDirection = new File(currentPath);
//            currentDirection.mkdir();
//        } else {
//            currentPath = path + file_separator + format + i;
//            currentDirection = new File(currentPath);
//        }
        Date date = new Date();
      //格式化并转换String类型
        currentPath = path + file_separator +  new SimpleDateFormat("yyyyMMdd").format(date);
	    //创建文件夹
	    currentDirection = new File(currentPath);
	    if(!currentDirection.exists())
	     currentDirection.mkdirs();
        
        
        
        //若文件夹里文件数目达到MAX_FILES_PER_DIR上限,即单个文件夹最大文件数目,则开新的文件夹目录
//        if (currentDirection.list().length >= MAX_FILES_PER_DIR) {
//            currentPath = path + file_separator + format + (i + 1);
//            currentDirection = new File(currentPath);
//            currentDirection.mkdir();
//        }
        if(name == null || name.equals(""))
			name = new StringBuilder(20).append(Thread.currentThread().getId())
					.append('-').append(System.currentTimeMillis()).append('.')
					.append(format).toString();
        File file = new File(currentDirection, name);
        try {
			if(!file.createNewFile()) {
				System.err.println("重复创建文件:" + file.getPath());
				file = null;
			}
		} catch (IOException e) {
			System.err.println("无法创建文件:" + e.getMessage());
			return null;
		}
        return file;
	}
	
	// this method test for a given name to store
	synchronized static public File createVideoFile(String format, String name) {
		return createFile(FileFactory.Type.Video, format, name);
	}
	
	//以下五个方法,均为二次调用方法 createFile(String type, String format)
	//我所以这样做而不是直接调用createFile方法,是为了减少同步块的大小------
	//只有获取同一类型文件(如同时获取两个视频类型文件)才需要考虑同步;同时获取
	//不同类型文件(如同时获取视频类型和图像类型文件)无须同步---------------
	synchronized static public File createVideoFile(String format) {
		return createFile(FileFactory.Type.Video, format);
	}
	
	synchronized static public File createAudioFile(String format) {
		return createFile(FileFactory.Type.Audio, format);
	}
	
	synchronized static public File createImageFile(String format) {
		return createFile(FileFactory.Type.Image, format);
	}
	
	synchronized static public File createPageFile(String format) {
		return createFile(FileFactory.Type.Page, format);
	}
	
	synchronized static public File createUnknownFile(String format) {
		return createFile(FileFactory.Type.Unknown, format);
	}
	
	static class Type {
		static String Video = "video";
		static String Audio = "audio";
		static String Image = "image";
		static String Page = "page";
		static String Unknown = "unknown";
	}

}