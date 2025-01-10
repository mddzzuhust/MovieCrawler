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

public class ParseQiChe {
	static int number=0;
    public static void filter(String url){
    	List<String> ls=new ArrayList<String>();
    	try{
    		
    		Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","list2");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
            
		    for(int i=0;i<list.size();i++){
		    	Tag tag=(Tag)list.elementAt(i);
		    	String string=tag.toHtml().toString();
		    	boolean flag=true;
		    	int index=0;
		    	while(true){
		    		index=string.indexOf("href",index);
		    		if(index<0) break;
		    		if(flag==false){
		    			flag=true;
		    			index=index+6;
		    			continue;
		    		}
		    		int last=string.indexOf("\"",index+6);
		    		String tmp=string.substring(index+6,last);
		    		//System.out.println(tmp);
		    		ls.add(tmp);
		    		index=index+6;
		    		flag=false;
		    	}
		    }
		    filter2(ls);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter2(List<String> ls){
    	Iterator iterator=ls.iterator();
    	boolean flag=true;
    	try{
    		while(iterator.hasNext()){
    			String url=iterator.next().toString();
    			Parser parser=new Parser();
    			parser.setEncoding(parser.getEncoding());
    		    URL urls = new URL(url);
    		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
    		    parser.setConnection(connection);
    		    String filename1=null,filename2,tmp;
    		    if(flag==true){
    		        NodeFilter classNF=new HasAttributeFilter("class","daohang");
    		        NodeList list=parser.extractAllNodesThatMatch(classNF);
    		        Tag tag=(Tag)list.elementAt(2);
    		        String string=tag.toHtml().toString();
    		        int last=string.indexOf("</a>");
    		        int index=string.lastIndexOf(">",last);
    		        filename1=string.substring(index+1,last);
    		        flag=false;
    		        System.out.println(filename1);
    		    }else{
    		    	parser.reset();
    		    	NodeFilter classNF=new HasAttributeFilter("class","t_b_title font14WithLine");
    		        NodeList list=parser.extractAllNodesThatMatch(classNF);
    		        Tag tag=(Tag)list.elementAt(0);
    		        String string=tag.toHtml().toString();
    		        int last=string.indexOf("</a>");
    		        int index=string.lastIndexOf(">",last);
    		        filename2=string.substring(index+1,last);
    		        System.out.println(filename2);
    		        parser.reset();
    		        classNF=new HasAttributeFilter("class","OptionDesc_2");
    		        list=parser.extractAllNodesThatMatch(classNF);
    		        tag=(Tag)list.elementAt(0);
    		        string=tag.toHtml().toString();
    		        index=string.indexOf("href");
    		        last=string.indexOf("\"",index+6);
    		        tmp=string.substring(index+6,last);
    		        System.out.println(tmp);
    		        if(filename1==null){
    		        	filename1="noband";
    		        }
    		        filter3(tmp,filename2,filename1);
    		    }
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter3(String url,String filename2,String filename1){
    	List<String> l=new ArrayList<String>();
    	l.add(url);
    	try{
    		Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter idNF=new HasAttributeFilter("id","Page");
	        NodeList list=parser.extractAllNodesThatMatch(idNF);
            while(list.size()>0){
            	Tag tag=(Tag)list.elementAt(0);
            	String string=tag.toHtml().toString();
            	int last=string.indexOf("下一页");
            	if(last<0) break;
            	int index=string.lastIndexOf("href",last);
            	last=string.indexOf("\"",index+6);
            	String tmp=string.substring(index+6,last);
            	l.add(tmp);
            	System.out.println(tmp);
            	
            	parser=new Parser();
    			parser.setEncoding(parser.getEncoding());
    		    urls = new URL(tmp);
    		    connection = (HttpURLConnection)urls.openConnection();     
    		    parser.setConnection(connection);
    		    
    		    idNF=new HasAttributeFilter("id","Page");
    	        list=parser.extractAllNodesThatMatch(idNF);
            }
            filter4(l,filename2,filename1);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter4(List<String> l,String filename2,String filename1){
    	List<String> ls=new ArrayList<String>();
    	List<String> filename3s=new ArrayList<String>();
    	Iterator iterator=l.iterator();
    	try{
    		while(iterator.hasNext()){
    		   String url=iterator.next().toString();
    		   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       URL urls = new URL(url);
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       parser.setConnection(connection);
		    
		       NodeFilter classNF=new HasAttributeFilter("class","r_link");
	           NodeList list=parser.extractAllNodesThatMatch(classNF);
               for(int i=0;i<list.size();i++){
            	   Tag tag=(Tag)list.elementAt(i);
            	   String string=tag.toHtml().toString();
            	   int index=string.indexOf("href");
            	   int last=string.indexOf("\"",index+6);
            	   String tmp=string.substring(index+6,last);
            	   System.out.println(tmp);
            	   ls.add(tmp);
            	   int flast=string.indexOf("款",last);
            	   if(flast<0){
            		   String filename3="noyear";
            		   filename3s.add(filename3);
            	   }else{
            		   int findex=string.lastIndexOf(">",flast);
            	       String filename3=string.substring(findex+1,flast);
            	       filename3s.add(filename3);
            	       System.out.println(filename3);
            	   }
               }
    		}
    		filter5(ls,filename3s,filename2,filename1);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter5(List<String> ls,List<String> filename3s,String filename2,String filename1){
    	Iterator iterator=ls.iterator();
    	Iterator iterator2=filename3s.iterator();
    	try{
    		while(iterator.hasNext()&&iterator2.hasNext()){
    			String url=iterator.next().toString();
    			String filename3=iterator.next().toString();
    			Parser parser=new Parser();
 			    parser.setEncoding(parser.getEncoding());
 		        URL urls = new URL(url);
 		        HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
 		        parser.setConnection(connection);
 		        
 		       NodeFilter classNF=new HasAttributeFilter("id","ImgOn");
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
        String url="http://photo.mycar168.com/25/";
        ParseQiChe.filter(url);
	}

}
