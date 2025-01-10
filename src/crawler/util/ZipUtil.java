package crawler.util;

import java.io.File;   
import java.io.FileInputStream;   
import java.io.FileOutputStream;   
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;   
import java.util.Enumeration;
import java.util.List;   
import java.util.zip.ZipEntry;   
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;   
  
public class ZipUtil {
  
    public boolean getZipFile(List<File> fileList, String zipFileName) throws Exception {
    	boolean flag = false;
    	InputStream in = null;
    	FileOutputStream fos = null;
    	ZipOutputStream zos = null;
    	ZipEntry entry = null;
    	File file = null;
    	byte[] b = new byte[4096];
    	int len = 0;
        try {
        	file = new File(zipFileName);
        	fos = new FileOutputStream(zipFileName);
        	zos = new ZipOutputStream(fos);
        	String prePath = file.getParent();
        	for (File srcfile : fileList) {
        		if(!srcfile.exists())
        			continue;
        		in = new FileInputStream(srcfile);
        		entry = new ZipEntry(srcfile.getAbsolutePath().replace(prePath, ""));
        		zos.putNextEntry(entry);
        		while ((len = in.read(b)) != -1) {
        			zos.write(b, 0, len);
        		}
        		in.close();
        	}
        	flag = true;
        } catch (Exception e) {
        	e.printStackTrace();
        	if (file.exists()) {
        		file.delete();
        	}
        } finally {
        	if (null != zos) {
        		zos.closeEntry();
        		zos.close();
        	}
        	if (null != fos) {
        		fos.close();
        	}
        	if(null != in) {
        		in.close();
        	}
        }
        
        if(flag) {
        	for(File srcfile : fileList) {
        		if(srcfile.exists()) {
        			if(!srcfile.isDirectory())
        				srcfile.delete();
        			else
        				delete(srcfile);
        		}
        	}
        }
        return flag;
    }
    
    public void delete(File file) {
    	if(file.exists()) {
    		if(!file.isDirectory())
    			file.delete(); //压缩后删除原文件
			else {
				File[] subFile = file.listFiles();
				for(File f : subFile) {
					delete(f);
				}
				file.delete();
			}
    	}
    }
  
    public List<File> getFileList(File root, String extName)
			throws Exception {
		List<File> fileList = new ArrayList<File>();
		if (!root.exists())
			return fileList;
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					fileList.addAll(getFileList(f, extName));
				} else if (!f.isHidden() && f.canRead()) {
					String name = f.getName();
					if (extName != null && !extName.isEmpty()) {
						if (extName.indexOf(name.substring(name
								.lastIndexOf('.') + 1)) != -1)
							fileList.add(f);
					} else {
						fileList.add(f);
					}
				}
			}
		} else {
			fileList.add(root);
		}

		return fileList;
	} 
    
    public boolean zipFile(String path, String extName) throws Exception {
    	List<File> fileList = getFileList(new File(path), extName);
    	if(fileList != null && !fileList.isEmpty()) {
    		String filename = null;
    		int beginIndex, endIndex;
    		if(path.endsWith(File.separator)) {
    			endIndex = path.lastIndexOf(File.separator) - 1;
    		} else {
    			endIndex = path.length();
    		}
    		beginIndex = path.lastIndexOf(File.separator, endIndex) + 1;
			filename = path.substring(beginIndex, endIndex) + ".zip";
    		return zipFile(fileList, path, filename);
    	}
    	
    	return false;
    }

    public boolean zipFile(String path, String extName, String filename) throws Exception {
    	List<File> fileList = getFileList(new File(path), extName);
    	if(fileList != null && !fileList.isEmpty()) {
    		return zipFile(fileList, path, filename);
    	}
    	
    	return false;
    }
    
    public boolean zipFile(List<File> fileList, String path, String filename) throws Exception {
    	if(path != null) {
    		boolean zip = false;
    		File root = new File(path);
    		filename = root.getParent() + File.separator + filename;
    		zip = getZipFile(fileList, filename);
    		delete(root);
    		
    		return zip;
    	}
    	
    	return false;
    }

	/**
	 * Decompression zip file
	 * @param zipfile: zip file name
	 * @param desDir: destination directory
	 * @throws Exception
	 */
    public void unZip(String zipfile, String desDir) throws Exception {
    	desDir = desDir.endsWith(File.separator) ? 
    			desDir : desDir + File.separator;

    	int length;
		byte b[] = new byte[8192];
		ZipFile zipFile;

		zipFile = new ZipFile(new File(zipfile));
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		ZipEntry zipEntry = null;
		while (enumeration.hasMoreElements()) {
			zipEntry = (ZipEntry) enumeration.nextElement();
			File loadFile = new File(desDir + zipEntry.getName());
			if (zipEntry.isDirectory()) {
				loadFile.mkdirs();
			} else {
				if (!loadFile.getParentFile().exists())
					loadFile.getParentFile().mkdirs();
				
				@SuppressWarnings("resource")
				OutputStream outputStream = new FileOutputStream(loadFile);
				InputStream inputStream = zipFile.getInputStream(zipEntry);
				while ((length = inputStream.read(b)) > 0)
					outputStream.write(b, 0, length);
			}
		}
	}
    
    public static void main(String[] args) throws Exception {
    	
    	ZipUtil zip = new ZipUtil();
//    	try {
//			boolean done = zip.zipFile("/idc/Crawler/test", null);
//			System.out.println(done);
//    		zip.unZip("/idc/Crawler/test.zip", "/idc/Crawler/test");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		String path = "/idc/Crawler/temp/page/";
		File file = new File(path);
		File[] subFiles = file.listFiles();
		for(File f : subFiles) {
			if(f.isDirectory()) {
				String zipPath = f.getAbsolutePath();
				zip.zipFile(zipPath, null);
				System.out.println("zip file:" + zipPath);
			}
		}
		
    }
}  