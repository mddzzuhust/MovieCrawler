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
public class ParseCar2 {
    static int count=0;
	public static void filter(String url){
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter idNF=new HasAttributeFilter("id","sortTable3");
		    NodeList list=parser.extractAllNodesThatMatch(idNF);
		    Tag tag=(Tag)list.elementAt(0);
		    String string=tag.toHtml();
		    //System.out.println(string.length());
		    //System.out.println(i);
		    int i=0;
		    int count=0;
		    while(i<string.length()){
		    	i=string.indexOf("<u>",i);
		    	if(i<0) break;
		    	int j=string.indexOf("</u>",i);
		    	String str=string.substring(i+3,j);
		    	//System.out.println(str);
		    	count++;
		    	System.out.print(count+" ");
		    	filter2(str);
		    	i=i+3;
		    	//break;
		    }
		    //System.out.println(count);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter2(String str){
		String url="http://newcar.xcar.com.cn/photo/";
		String tmp="http://newcar.xcar.com.cn";
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter idNF=new HasAttributeFilter("id","treebox_list");
		    NodeList list=parser.extractAllNodesThatMatch(idNF);
		    Tag tag=(Tag)list.elementAt(0);
		    //System.out.println(tag.toPlainTextString());
		    //System.out.println(++count);
		    String string=tag.toHtml();
		    int i=0;
		    while(i<string.length()){
		    	i=string.indexOf("<em>",i);
		    	if(i<0)  break;
		    	int j=string.lastIndexOf('>',i);
		    	//System.out.println(string.substring(j+1,i));
		    	if(str.indexOf(string.substring(j+1,i))>=0){
		    		/*System.out.print(string.substring(j+1,i)+" ");
		    		System.out.println(++count);*/
		    		int k=string.lastIndexOf("href",j);
		    		int l=string.lastIndexOf("id=",j);
		    		String temp=string.substring(k+6,l-1);
		    		temp=temp.substring(0,temp.length()-1);
		    		tmp=tmp+temp;
		    		//System.out.println(tmp);
		    		filter3(tmp,str);
		    		tmp="http://newcar.xcar.com.cn";
		    	}
		    	i=i+4;
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter3(String url,String style){
		String tmp="http://newcar.xcar.com.cn";
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","open_stop");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    //System.out.println(list.size());
		    for(int i=0;i<list.size();i++){
		    	Tag tag=(Tag)list.elementAt(i);
		    	//System.out.println(tag.toHtml());
		    	String string=tag.toHtml();
		    	int j=string.indexOf("<em>");
		    	int k=string.lastIndexOf('>',j);
		    	String temp=string.substring(k+1,j);
		    	//System.out.println(temp);
		    	if(style.lastIndexOf(temp)>=0){
		    		//System.out.print(temp);
		    		int before=string.lastIndexOf("href",j);
		    		int last=string.indexOf("title",before);
		    		tmp=tmp+string.substring(before+6,last);
		    		tmp=tmp.substring(0,tmp.length()-2);
		    		//System.out.println(tmp);
		    		filter4(tmp,style);
		    		tmp="http://newcar.xcar.com.cn";
		    	}
		    }
		    System.out.println();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter4(String url,String style){
		String tmp="http://newcar.xcar.com.cn";
		try{
			int p1=url.indexOf("/photo/");
			int p2=url.lastIndexOf("/photo/");
			System.out.println(p1+" "+p2);
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("title","整体外观");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    //System.out.println(list.size());
		    if(list.size()>0){
		    Tag tag=(Tag)list.elementAt(0);
		    //System.out.println(tag.toHtml());
		    String string=tag.toHtml();
		    int i=string.indexOf("href");
		    int j=string.indexOf("title");
		    String temp=string.substring(i+6,j);
		    tmp=tmp+temp.substring(0,temp.length()-2);
		    System.out.println(tmp);
		    tmp="http://newcar.xcar.com.cn";
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        String url="http://db.auto.sohu.com/cxdata/salesindex.html";
        ParseCar2.filter(url);
	}

}
