package crawler.parse;

import java.net.HttpURLConnection;
import java.net.URL;
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
import org.htmlparser.Parser;

public class ParseChe {
	static int number=0;
	public static void filter(String url){
		String str="http://pic.16888.com";
        List<String> ls=new ArrayList<String>();
        List<String> filename2s=new ArrayList<String>();
		try{
    	    Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","car_pic_box");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    
		    for(int i=0;i<list.size();i++){
		    	Tag tag=(Tag)list.elementAt(i);
		    	String string=tag.toHtml().toString();
		    	int index=0;
		    	boolean flag=true;
		    	while(true){
		    		index=string.indexOf("href",index);
		    		if(index<0) break;
		    		if(flag==false){
		    			flag=true;
		    			index=index+6;
		    			continue;
		    		}
		    		int last=string.indexOf("\"",index+6);
		    		String tmp=str+string.substring(index+6,last);
		    		ls.add(tmp);
		    		//System.out.println(tmp);
		    		int fin=string.indexOf("title",index);
		    		int fla=string.indexOf("\"",fin+7);
		    		String filename2=string.substring(fin+7,fla);
		    		filename2s.add(filename2);
		    		//System.out.println(filename2);
		    		index=index+6;
		    		flag=false;
		    	}
		    }
		    
		    parser.reset();
		    classNF=new HasAttributeFilter("class","f_l");
		    list=parser.extractAllNodesThatMatch(classNF);
		    Tag tag=(Tag)list.elementAt(0);
		    String string=tag.toHtml().toString();
		    int last=string.indexOf("</span>");
		    int index=string.lastIndexOf("&gt;",last);
		    //System.out.println(string.substring(index+5,last-2));
		    String filename1=string.substring(index+5,last-2);
		    
		    filter2(ls,filename2s,filename1);
       }catch(Exception e){
    	   e.printStackTrace();
       }
    }
	
	public static void filter2(List<String> ls,List<String> filename2s,String filename1){
		System.out.println(filename1);
		String str="http://pic.16888.com";
		List<String> l=new ArrayList<String>();
		Iterator iterator=ls.iterator();
		Iterator iterator1=filename2s.iterator();
		try{
			while(iterator.hasNext()&&iterator1.hasNext()){
				l.clear();
				String url=iterator.next().toString();
				String filename2=iterator1.next().toString();
				//System.out.println(filename2);
				Parser parser=new Parser();
				parser.setEncoding(parser.getEncoding());
			    URL urls = new URL(url);
			    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
			    parser.setConnection(connection);
			    
			    NodeFilter classNF=new HasAttributeFilter("class","brand_car");
			    NodeList list=parser.extractAllNodesThatMatch(classNF);
			    
			    Tag tag=(Tag)list.elementAt(0);
			    String string=tag.toHtml().toString();
			    int index=0;
			    while(true){
			    	index=string.indexOf("href",index);
			    	if(index<0) break;
			    	int last=string.indexOf("\"",index+6);
			    	String tmp=str+string.substring(index+6,last);
			    	l.add(tmp);
			    	//System.out.println(tmp);
			    	index=index+6;
			    }
			    if(l.size()<1) continue;
			    filter3(l,filename2,filename1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter3(List<String> l,String filename2,String filename1){
		String str="http://pic.16888.com";
		Iterator iterator=l.iterator();
		try{
	        while(iterator.hasNext()){
	        	String url=iterator.next().toString();
	        	Parser parser=new Parser();
				parser.setEncoding(parser.getEncoding());
			    URL urls = new URL(url);
			    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
			    parser.setConnection(connection);
			    
			    NodeFilter classNF=new HasAttributeFilter("class","brand_list");
			    NodeList list=parser.extractAllNodesThatMatch(classNF);
			    
			    Tag tag=(Tag)list.elementAt(0);
			    String string=tag.toHtml().toString();
			    int index=string.indexOf("href");
			    index=index+6;
			    index=string.indexOf("href",index);
			    int last=string.indexOf("\"",index+6);
			    String tmp=str+string.substring(index+6,last);
			    
			    parser.reset();
			    classNF=new HasAttributeFilter("class","selected");
			    list=parser.extractAllNodesThatMatch(classNF);
			    tag=(Tag)list.elementAt(0);
			    string=tag.toHtml().toString();
			    
			    last=string.indexOf("</i>");
			    index=string.lastIndexOf(">",last);
			    String filename3=string.substring(index+1,last);
			    
			    filter4(tmp,filename3,filename2,filename1);
			 }		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter4(String url,String filename3,String filename2,String filename1){
		String str="http://pic.16888.com";
		List<String> ls=new ArrayList<String>();
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","show_cars");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    
		    Tag tag=(Tag)list.elementAt(0);
		    String string=tag.toHtml().toString();
		    boolean flag=true;
		    int index=0;
		    while(true){
		    	index=string.indexOf("href",index);
		    	if(index<0) break;
		    	if(flag==false){
		    		index=index+6;
		    		flag=true;
		    		continue;
		    	}
		    	int last=string.indexOf("\"",index+6);
		    	String tmp=str+string.substring(index+6,last);
		    	ls.add(tmp);
		    	flag=false;
		    }
		    filter5(ls,filename3,filename2,filename1);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter5(List<String> ls,String filename3,String filename2,String filename1){
		Iterator iterator=ls.iterator();
		try{
			while(iterator.hasNext()){
				String url=iterator.next().toString();
				Parser parser=new Parser();
				parser.setEncoding(parser.getEncoding());
			    URL urls = new URL(url);
			    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
			    parser.setConnection(connection);
			    
			    NodeFilter classNF=new HasAttributeFilter("class","pic_main");
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
        String url="http://pic.16888.com/b/57229/";
        ParseChe.filter(url);
	}

}
