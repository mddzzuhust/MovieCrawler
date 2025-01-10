package crawler.parse;

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

public class ParseClothes {
	static int count=0;
    public static void filter(String url){ 
    	List<String> ls=new ArrayList<String>();
    	String str="http://www.amazon.cn";
    	int num=1;
    	ls.add(url);
    	//filter2("http://www.amazon.cn/s?ie=UTF8&page=2&rh=n%3A2154404051");
    	System.out.println(url+"  "+1);
    	//filter2(url);
    	try{   
    		while(true){
    		   Parser parser=new Parser();
    		   parser.setEncoding(parser.getEncoding());
    		   URL urls = new URL(url);
    		   HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
    		   //connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
    		   parser.setConnection(connection);
		       
		       NodeFilter classNF=new HasAttributeFilter("class","pagnNext");
		       NodeList list=parser.extractAllNodesThatMatch(classNF);
		       if(list.size()<1) break;
		       Tag tag=(Tag)list.elementAt(0);
		       String string=tag.toHtml();
		       //System.out.println(string);
		       int i=string.indexOf("href");
		       int j=string.indexOf('"',i+6);
		       String tmp=string.substring(i+6,j);
		       tmp=str+tmp;
		       System.out.println(tmp+"  "+(++num));
		       //filter2(tmp);
		       ls.add(tmp);
		       if(num<20)
		          url=tmp;
		       else
		    	  break;
		       
		       //System.out.println(tmp+"  "+num);
    	       //System.out.println(list.size());
    	       /*for(int k=0;k<list.size();k++){
		         Tag tag=(Tag)list.elementAt(k);
		         String string=tag.toHtml();
		         //System.out.println(string);
		         int i=string.indexOf("href=");
		         int j=string.indexOf('"',i+6);
		         String tmp=string.substring(i+6,j);
		         //System.out.println(tmp+"  "+(k+1));
		         ls.add(tmp);
    	       }*/
    	       /*Iterator iterator=ls.iterator();
    	       while(iterator.hasNext()){
    	    	   System.out.println(iterator.next().toString());
    	       }*/
    		}
    		filter2(ls);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter2(List<String> lst){
    	List<String> ls=new ArrayList<String>();
    	try{
    	   Iterator iterator = lst.iterator();
    	   while(iterator.hasNext()){
    	   String url=iterator.next().toString();
    	   Parser parser=new Parser();
		   parser.setEncoding(parser.getEncoding());
		   URL urls = new URL(url);
		   HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		   //connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		   parser.setConnection(connection);
		   
		   NodeFilter classNF=new HasAttributeFilter("class","a-row a-gesture a-gesture-horizontal");
	       NodeList list=parser.extractAllNodesThatMatch(classNF);
	       
	       System.out.println(url+"  "+list.size());
	       for(int k=0;k<list.size();k++){
		         Tag tag=(Tag)list.elementAt(k);
		         String string=tag.toHtml();
		         //System.out.println(string);
		         int i=string.indexOf("href=");
		         int j=string.indexOf('"',i+6);
		         String tmp=string.substring(i+6,j);
		         //System.out.println(tmp+"  "+(k+1));
		         ls.add(tmp);
  	       }
	       filter3(ls);
    	   }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter3(List<String> ls){
    	//System.out.println(ls.size());
    	Iterator iterator=ls.iterator();
    	while(iterator.hasNext()){
    		String s=iterator.next().toString();
    		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(s);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("id","imgTagWrapperId");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    //System.out.println(list.size()+"  "+count);
    		Tag tag=(Tag)list.elementAt(0);
    		String string=tag.toHtml();
    		//System.out.println(string);
    		int i=string.indexOf("http");
    		i=string.indexOf("http",i+4);
    		int j=string.indexOf(".jpg",i+4);
    		String str=string.substring(i, j+4);
    		//System.out.println(str);
    		downloadImg(str,"D:/aaa/bbb",String.valueOf(++count)+".jpg");
    		System.out.println("下载成功");
    		//break;
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void downloadImg(String imgSrc,String filePath,String fileName){
    	try{
    		URL url = new URL(imgSrc);
    		URLConnection conn = url.openConnection();
    		conn.setConnectTimeout(10000*1000); 
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
        String url="http://www.amazon.cn/s/ref=lp_2152154051_nr_n_17?fst=as%3Aoff&rh=n%3A2016156051%2Cn%3A%212016157051%2Cn%3A2152154051%2Cn%3A2154374051&bbn=2152154051&ie=UTF8&qid=1445480327&rnid=2152154051";
        filter(url);
	}

}
