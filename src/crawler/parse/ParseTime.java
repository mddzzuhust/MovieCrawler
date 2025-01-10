package crawler.parse;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.Parser;

public class ParseTime {
	static int number=0;
    public static void filter(String url){
    	String str="http://car.autotimes.com.cn";
    	List<String> ls=new ArrayList<String>();
    	List<String> filename2s=new ArrayList<String>();
    	String filename2;
    	try{
    		Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","rp_t_1");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    Tag tag=(Tag)list.elementAt(list.size()-1);
		    String string=tag.toHtml().toString();
		    int last=string.lastIndexOf("</a>");
		    int index=string.lastIndexOf(">",last);
		    String filename1=string.substring(index+1,last);
		    System.out.println(filename1);
            parser.reset();
            classNF=new HasAttributeFilter("class","quop_img_10");
		    list=parser.extractAllNodesThatMatch(classNF);
		    for(int i=0;i<list.size();i++){
            	tag=(Tag)list.elementAt(i);
                string=tag.toHtml().toString();
                index=string.indexOf("href");
                last=string.indexOf("\"",index+6);
                String tmp=str+"/pic"+string.substring(index+8,last);
                //System.out.println(tmp);
                ls.add(tmp);
                int fin=string.indexOf("title");
                int fla=string.indexOf("\"",fin+7);
                filename2=string.substring(fin+7, fla);
                //System.out.println(filename2);
                filename2s.add(filename2);
            }
		    filter2(ls,filename2s,filename1);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter2(List<String> ls,List<String> filename2s,String filename1){
    	String str="http://car.autotimes.com.cn/pic";
    	Iterator iterator=ls.iterator();
    	Iterator iterator2=filename2s.iterator();
    	try{
    	   while(iterator.hasNext()&&iterator.hasNext()){
    		   String url=iterator.next().toString();
    		   String filename2=iterator2.next().toString();
    		   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       URL urls = new URL(url);
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       parser.setConnection(connection);
		       
		       NodeFilter classNF=new HasAttributeFilter("class","quop_right_32");
			   NodeList list=parser.extractAllNodesThatMatch(classNF);
               Tag tag=(Tag)list.elementAt(0);
               String string=tag.toHtml().toString();
               int index=string.indexOf("href");
               int last=string.indexOf("\"",index+6);
               String tmp=str+string.substring(index+8,last);
               //System.out.println(tmp);
               filter3(tmp,filename2,filename1);
    	    }
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter3(String url,String filename2,String filename1){
    	String str="http://car.autotimes.com.cn/pic";
    	List<String> ls=new ArrayList<String>();
    	ls.add(url);
    	try{
    		while(true){
    		   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       URL urls = new URL(url);
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       parser.setConnection(connection);
		       
		       NodeFilter classNF=new HasAttributeFilter("class","quop_pplb_46");
			   NodeList list=parser.extractAllNodesThatMatch(classNF);
			   Tag tag=(Tag)list.elementAt(0);
			   String string=tag.toHtml().toString();
			   int last=string.indexOf("下一页");
			   if(last<0) break;
			   int index=string.lastIndexOf("href",last);
			   last=string.indexOf("\"",index+6);
			   String tmp=str+string.substring(index+8,last);
			   ls.add(tmp);
			   //System.out.println(tmp);
			   url=tmp;
    		}
    		filter4(ls,filename2,filename1);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter4(List<String> ls,String filename2,String filename1){
    	String str="http://car.autotimes.com.cn/pic";
    	List<String> l=new ArrayList<String>();
    	List<String> filename3s=new ArrayList<String>();
    	Iterator iterator=ls.iterator();
    	try{
    	    while(iterator.hasNext()){
    	    	 l.clear();
    	    	 filename3s.clear();
    		     String url=iterator.next().toString();
    		     Parser parser=new Parser();
  			     parser.setEncoding(parser.getEncoding());
  		         URL urls = new URL(url);
  		         HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
  		         parser.setConnection(connection);
  		         
  		         NodeFilter classNF=new HasAttributeFilter("class","quop_img_10");
			     NodeList list=parser.extractAllNodesThatMatch(classNF);
    	         for(int i=0;i<list.size();i++){
    	        	 Tag tag=(Tag)list.elementAt(i);
    	        	 String string=tag.toHtml().toString();
    	        	 int index=string.indexOf("href");
    	        	 int last=string.indexOf("\"",index+6);
    	        	 String tmp=str+string.substring(index+8,last);
    	        	 l.add(tmp);
    	        	 int fin=string.indexOf("alt");
    	        	 int fla=string.indexOf("款",fin);
    	        	 fin=string.lastIndexOf("\"",fla);
    	        	 String filename3=string.substring(fin+1,fla);
    	        	 filename3s.add(filename3);
    	         }
    	         filter5(l,filename3s,filename2,filename1);
    	    }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter5(List<String> l,List<String> filename3s,String filename2,String filename1){
    	Iterator iterator=l.iterator();
    	Iterator iterator2=filename3s.iterator();
    	try{
    		while(iterator.hasNext()&&iterator2.hasNext()){
    			String url=iterator.next().toString();
    			String filename3=iterator2.next().toString();
    			Parser parser=new Parser();
 			    parser.setEncoding(parser.getEncoding());
 		        URL urls = new URL(url);
 		        HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
 		        parser.setConnection(connection);
    			
 		        NodeFilter classNF=new HasAttributeFilter("class","quop_img_22");
			    NodeList list=parser.extractAllNodesThatMatch(classNF);
			    Tag tag=(Tag)list.elementAt(0); 
			    String string=tag.toHtml().toString();
			    int index=string.indexOf("src");
			    int last=string.indexOf("\"",index+5);
			    String pic=string.substring(index+5,last);
			    predownloadImg(pic,filename1,filename2,filename3);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void predownloadImg(String url,String filename1,String filename2,String filename3){
    	String filePath=makefile(filename1,filename2,filename3);
    	downloadImg(url,filePath,String.valueOf(++number)+".jpg");
    	System.out.println("Yeah!");
    }
    
    public static String makefile(String filename1,String filename2,String filename3){
    	filename1=filename1.replaceAll(" ","");
    	filename2=filename2.replaceAll(" ","");
    	filename3=filename3.replaceAll(" ","");
    	File file=new File("D:/Car/"+filename1);
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		file=new File("D:/Car/"+filename1+"/"+filename2);
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		file=new File("D:/Car/"+filename1+"/"+filename2+"/"+filename3+"/");
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		return file.toString();
    }
    
    public static void downloadImg(String imgSrc,String filePath,String fileName){
    	try{
    		URL url = new URL(imgSrc);
    		URLConnection conn = url.openConnection();
    		conn.setConnectTimeout(300*1000); 
    		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
    		InputStream str = conn.getInputStream(); 
    		byte[] bs = new byte[1024];  
    		int len = 0; 
    		File saveDir = new File(filePath);    
            if(!saveDir.exists()){    
                saveDir.mkdir();    
            }    
            File file = new File(saveDir+File.separator+fileName); 
            //实例输出一个对象  
            FileOutputStream out = new FileOutputStream(file);  
            //循环判断，如果读取的个数b为空了，则is.read()方法返回-1，具体请参考InputStream的read();  
            while ((len = str.read(bs)) != -1) {  
                //将对象写入到对应的文件中  
                out.write(bs, 0, len);     
            }  
  
            //刷新流  
            out.flush();  
            //关闭流  
            out.close();  
            str.close();  
              
            //System.out.println("下载成功"); 
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
	
    public static void main(String[] args) {
		// TODO Auto-generated method stub
        String url="http://car.autotimes.com.cn/pic/ab9/";
        ParseTime.filter(url);
	}

}
