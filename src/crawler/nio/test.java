package crawler.nio;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class test {
	public static void main(String[] args) throws Exception{
		LineIterator it = null;
		try {
			/*it = FileUtils.lineIterator(new File("D:\\CrawlerDownload\\urlCrawled\\urls1.txt"), "UTF-8");
			
		    while (it.hasNext()) {
		        final String line = it.nextLine();
		        System.out.println(line);
		    }*/
		    
		  //final List<String> lines = FileUtils.readLines(new File(""), Charsets.UTF_8);
		    for (String line : Files.readLines(new File("D:/CrawlerDownload/urlCrawled/urls1.txt"), Charsets.UTF_8)) {
	            System.out.println(line);
	        }
		} finally {
		   // it.close();
		}
	}
	
}
