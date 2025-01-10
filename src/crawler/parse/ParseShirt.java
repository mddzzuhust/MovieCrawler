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

public class ParseShirt {
    public static void filter(String url){
        List<String> l=new ArrayList<String>();
    	try{
    	   
    	   Parser parser=new Parser();
    	   parser.setEncoding(parser.getEncoding());
    	   URL urls = new URL(url);
		   HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		   connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		   parser.setConnection(connection);
		   
		   NodeFilter AttrNF=new HasAttributeFilter("class","a-link-normal a-text-normal");
		   NodeList list=parser.extractAllNodesThatMatch(AttrNF);
		   //System.out.println(list.size());
		   boolean flag=true;
		   for(int i=0;i<list.size();i++){
			   Tag tag=(Tag)list.elementAt(i);
			   String str=tag.toHtml().toString();
			   //System.out.println(str);
			   int index=str.indexOf("href");
			   int last=str.indexOf("\"",index+6);
			   String substr=str.substring(index+6,last);
			   String tmp=substr.substring(0,4);
			   if(!tmp.equals("http"))
				   continue;
			   //download(substr,String.valueOf(i+51)+".jpg");
			   if(flag==true){
				   l.add(substr);
			       flag=false;
			   }else{
				   flag=true;
			   }
		   }
		   filter2(l);
       }catch(Exception e){
    	   e.printStackTrace();
       }
    }
    
    public static void filter2(List<String> list){
    	try{
    		Iterator iterator=list.iterator();
    		int number=102;
    		while(iterator.hasNext()){
    			String url=iterator.next().toString();
    			Parser parser=new Parser();
    	    	parser.setEncoding(parser.getEncoding());
    	    	URL urls = new URL(url);
    			HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
    			connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
    			parser.setConnection(connection);
    		
    			NodeFilter AttrNF=new HasAttributeFilter("id","landingImage");
    			NodeList lst=parser.extractAllNodesThatMatch(AttrNF);
    			Tag tag=(Tag)lst.elementAt(0);
    			String str=tag.toHtml().toString();
    			int index=str.indexOf("http:");
    			index=str.indexOf("http:",index+5);
    			int last=str.indexOf(".jpg",index);
    			if(index>=last+4) continue;
    			if(index>=last+4) continue;
    			String img=str.substring(index, last+4);
    			download(img,String.valueOf(++number)+".jpg");
    			System.out.println(number);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void download(String imgsrc,String fileName){
    	try{
    	    URL url=new URL(imgsrc);
    	    URLConnection conn = url.openConnection();
    	    conn.setConnectTimeout(3*1000); 
    		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
    		InputStream str = conn.getInputStream(); 
    		
    		byte[] bs = new byte[1024];  
    		int len = 0; 
    		File saveDir=new File("E:\\pic\\pants");
    		if(!saveDir.exists()){    
                saveDir.mkdir();    
            }    
    		File file = new File(saveDir+File.separator+fileName);
    		FileOutputStream out = new FileOutputStream(file); 
    		while ((len = str.read(bs)) != -1) {  
                //将对象写入到对应的文件中  
                out.write(bs, 0, len);     
            }  
            //刷新流  
            out.flush();  
            //关闭流  
            out.close();  
            str.close();  
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
       ParseShirt.filter("http://www.amazon.cn/s/ref=sr_pg_6?rh=n%3A2016156051%2Cn%3A%212016157051%2Cn%3A2152154051%2Cn%3A2154392051&page=6&ie=UTF8&qid=1452821044&spIA=B0196UJ5BY,B018FROKIW,B018O356QC,B01962AAS4,B01962BRXQ,B0196FM8DQ,B0185MC9YY,B0196E1W5M,B0191S0HO0,B018U3AX78,B0196CROPQ,B01962AASO");
	}

}
