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

public class ParseYi {
    static int number=0;
	public static void filter(String url){
	    String str="http://photo.bitauto.com";
	    List<String> ls=new ArrayList<String>();
		try{
	    	Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter idNF=new HasAttributeFilter("id","level0");
		    NodeList list=parser.extractAllNodesThatMatch(idNF);
		    
		    //System.out.println(list.size());
		    Tag tag=(Tag)list.elementAt(0);
		    String string=tag.toHtml().toString();
		    //System.out.println(string);
		    
		    int i=0;
		    boolean flag=true;
		    while(true){
		    	i=string.indexOf("carpic_list left_list",i);
		    	//System.out.println(i);
		    	if(i<0) break;
		    	int mark=string.indexOf("title-con-2",i);
		    	if(mark<0)  mark=string.length()-1;
		    	//System.out.println(mark);
		    	int index=i;
		    	while(true){
		    		index=string.indexOf("href",index);
		    		if(index>mark||index<0){ 
		    		   break;
		    		}
		    		if(flag==true){
		    			int last=string.indexOf("\"",index+6);
		    			String tmp=str+string.substring(index+6,last);
		    			ls.add(tmp);
		    			//System.out.println(tmp);
		    			index=index+6;
		    			flag=false;
		    		}else{
		    			index=index+6;
		    			flag=true;
		    			continue;
		    		}
		    	}
		    	i=i+20;
		        flag=true;
		        //break;
		    }
		    //System.out.println(ls.size());
		    parser.reset();
		    NodeFilter classNF=new HasAttributeFilter("class","tree_navigate");
		    list=parser.extractAllNodesThatMatch(classNF);
		    tag=(Tag)list.elementAt(0);
		    string=tag.toHtml().toString();
		    int index=string.indexOf("<strong>");
		    int last=string.indexOf("</strong>");
		    String filename1=string.substring(index+8,last);
		    //System.out.println(filename1);
		    
		    filter2(ls,filename1);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	public static void filter2(List<String> ls,String filename1){
		String str="http://photo.bitauto.com";
		List<String> l=new ArrayList<String>();
		Iterator iterator=ls.iterator();
		try{
			while(iterator.hasNext()){
				  l.clear();
				  String url=iterator.next().toString();
				  l.add(url);
				  //System.out.println(url);
	       		  Parser parser=new Parser();
	   			  parser.setEncoding(parser.getEncoding());
	   		      URL urls = new URL(url);
	   		      HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
	   		      parser.setConnection(connection);
	   		      
	   		      NodeFilter classNF=new HasAttributeFilter("id","caryear_container");
			      NodeList list=parser.extractAllNodesThatMatch(classNF);
			      //System.out.println(list.size());
			      if(list.size()<1) continue;
			      Tag tag=(Tag)list.elementAt(0);
			      String string=tag.toHtml().toString();
			      int index=0;
			      while(true){
			    	  index=string.indexOf("href",index);
			    	  if(index<0) break;
			    	  int last=string.indexOf("\"",index+6);
			    	  String tmp=string.substring(index+6,last);
			    	  if(tmp.length()==0){ 
			    		  index=index+6;
			    		  continue;
			    	  }
			    	  tmp=str+tmp;
			    	  l.add(tmp);
			    	  //System.out.println(tmp);
			    	  index=index+6;
			      }
			      
			      parser.reset();
			      classNF=new HasAttributeFilter("class","title-box");
				  list=parser.extractAllNodesThatMatch(classNF);
				  //System.out.println(list.size());
				  tag=(Tag)list.elementAt(0);
				  string=tag.toHtml().toString();
				  index=string.indexOf("<h3>");
				  int last=string.indexOf("</h3>");
				  String filename2=string.substring(index+4,last);
				  //System.out.println(filename2);
				  filter3(l,filename1,filename2);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter3(List<String> ls,String filename1,String filename2){		
		String str="http://photo.bitauto.com";
		Iterator iterator=ls.iterator();
		try{
			while(iterator.hasNext()){
				  String url=iterator.next().toString();
				  Parser parser=new Parser();
	   			  parser.setEncoding(parser.getEncoding());
	   		      URL urls = new URL(url);
	   		      HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
	   		      parser.setConnection(connection);
	   		      
	   		      NodeFilter classNF=new HasAttributeFilter("id","group_container");
	   		      NodeList list=parser.extractAllNodesThatMatch(classNF);
	   		      if(list.size()<1) continue;
	   		      Tag tag=(Tag)list.elementAt(0);
	   		      String string=tag.toHtml().toString();
	   		      int index=string.indexOf("href=");
	   		      index=string.indexOf("href=",index+6);
	   		      int last=string.indexOf("\"",index+6);
	   		      String tmp=string.substring(index+6,last);
	   		      tmp=str+tmp;
	   		      System.out.println(tmp);
	   		      parser.reset();
	   		      classNF=new HasAttributeFilter("id","caryear_container");
	   		      list=parser.extractAllNodesThatMatch(classNF);
	   		      tag=(Tag)list.elementAt(0);
	   		      string=tag.toHtml().toString();
	   		      index=string.indexOf("<a href=\"\">");
	   		      last=string.indexOf("</a>",index);
	   		      String filename3=string.substring(index+11,last);
	   		      System.out.println(filename3);
	   		      filter4(tmp,filename1,filename2,filename3);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter4(String url,String filename1,String filename2,String filename3){
		String str="http://photo.bitauto.com";
		List<String> ls=new ArrayList<String>();
		ls.add(url);
		try{
			  while(true){
			     Parser parser=new Parser();
 			     parser.setEncoding(parser.getEncoding());
 		         URL urls = new URL(url);
 		         HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
 		         parser.setConnection(connection);
 		      
 		         NodeFilter classNF=new HasAttributeFilter("class","next_on");
 		         NodeList list=parser.extractAllNodesThatMatch(classNF);
 		         if(list.size()<1){
 		    	    ls.add(url);
 		    	    break;
 		         }
 		         Tag tag=(Tag)list.elementAt(0);
 		         String string=tag.toHtml().toString();
 		         int index=string.indexOf("href=");
 		         int last=string.indexOf("\"",index+6);
 		         String tmp=string.substring(index+6,last);
 		         url=str+tmp;
 		         System.out.println(url);
			  }
			  filter5(ls,filename1,filename2,filename3);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void filter5(List<String> ls,String filename1,String filename2,String filename3){
		String str="http://photo.bitauto.com";
		List<String> l=new ArrayList<String>();
		Iterator iterator=ls.iterator();
		try{
			while(iterator.hasNext()){
				  String url=iterator.next().toString();
				  Parser parser=new Parser();
	   			  parser.setEncoding(parser.getEncoding());
	   		      URL urls = new URL(url);
	   		      HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
	   		      parser.setConnection(connection);
	   		      
	   		      NodeFilter classNF=new HasAttributeFilter("class","carpic_list");
	   		      NodeList list=parser.extractAllNodesThatMatch(classNF);
	   		      Tag tag=(Tag)list.elementAt(0);
	   		      String string=tag.toHtml().toString();
	   		      int index=0;
	   		      boolean flag=true;
	   		      while(true){
	   		    	  index=string.indexOf("href=",index);
	   		    	  if(index<0) break;
	   		    	  if(flag==false){
	   		    		  index=index+6;
	   		    		  flag=true;
	   		    	  }
	   		    	  int last=string.indexOf("\"",index+6);
	   		    	  String tmp=string.substring(index+6,last);
	   		    	  tmp=str+tmp;
	   		    	  l.add(tmp);
	   		    	  flag=false;
	   		    	  index=index+6;
	   		      }
			}
			filter6(l,filename1,filename2,filename3);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter6(List<String> l,String filename1,String filename2,String filename3){
		Iterator iterator=l.iterator();
		try{
		     while(iterator.hasNext()){
			   String url=iterator.next().toString();
			   Parser parser=new Parser();
 			   parser.setEncoding(parser.getEncoding());
 		       URL urls = new URL(url);
 		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
 		       parser.setConnection(connection);
		      
		       NodeFilter classNF=new HasAttributeFilter("class","pic_box");
		       NodeList list=parser.extractAllNodesThatMatch(classNF);
		       Tag tag=(Tag)list.elementAt(0);
		       String string=tag.toHtml().toString();
		       
		       int index=string.indexOf("src=");
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
        ParseYi.filter("http://photo.bitauto.com/master/92/");
	}

}
