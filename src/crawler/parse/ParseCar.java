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

public class ParseCar {
	static int index=0;
	static int numberall=0;
	public static void filter(String url){ 
	    List<String> sales=new ArrayList<String>();
	    try{
	    	Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter AttrNF=new HasAttributeFilter("id","mainIframe");
		    NodeList list=parser.extractAllNodesThatMatch(AttrNF);
		    Tag tag=(Tag)list.elementAt(0);
		    String str=tag.getAttribute("src");
		    String url1=url+str;
		    //System.out.println(str);
		    System.out.println(list.size());
		    //sales=filter1(url1,url);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	public static List<String> filter1(String url1,String url){
		List<String> sales=new ArrayList<String>();
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url1);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter divNF = new NodeClassFilter(Div.class);
			NodeFilter divAttNF = new HasAttributeFilter("id","sales3");
			NodeFilter divAF = new AndFilter(new NodeFilter[]{divNF, divAttNF});
			NodeList list=parser.extractAllNodesThatMatch(divAF);
			//System.out.println(list.size());
			Tag tag=(Tag)list.elementAt(0);
			String str=tag.toHtml();
			//System.out.println(str);
			int i=0;
			while(i<str.length()){
				i=str.indexOf("href",i);
				if(i<0)  break;
				int j=str.indexOf('"',i+6);
				//System.out.println(str.substring(i+6,j));
				sales.add(str.substring(i+6,j));
				i=i+6;
			}
			/*System.out.println(sales.size());
			Iterator<String> iterator=sales.iterator();
			while(iterator.hasNext()){
				System.out.println(iterator.next());
			}*/
			/*for(int j=0;j<sales.size();j++){
				System.out.println(sales.toString());
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}
		List<String> result=new ArrayList<String>();
		result.addAll(filter2(sales,url));
		//result.addAll(filter2_1(sales,url));
		return result;
	}
	
	public static List<String> filter2(List<String> sales,String url){
		List<String> dep=new ArrayList<String>();
		
		try{
			Iterator<String> iterator=sales.iterator();
			//System.out.println(sales.size());
		    while(iterator.hasNext()){
		       Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       String tmp=iterator.next().toString();
		       String u=url+tmp;
			   URL urls = new URL(u);
			   //System.out.println(u);
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		       parser.setConnection(connection);
		       
		       NodeFilter imgNF=new HasAttributeFilter("class","contentD");
		       NodeList list=parser.extractAllNodesThatMatch(imgNF);
		       
		       Tag tag=(Tag)list.elementAt(0);
		       String str=tag.toHtml();
		       //System.out.println(str);
		       int count=0;
		       int i=0;
		       while(i<str.length()){
		    	   i=str.indexOf("href",i);
		    	   if(i<0) break;
		    	   int j=str.indexOf('"',i+6);
		    	   String temp=str.substring(i+6,j);
		    	   //System.out.println(temp);
		    	   count++;
		    	   if(count==4){
		    		   //System.out.println(temp);
		    		   dep=filter3(temp);//false表示不是官方图片
		    		   dep=filter2_1(temp);//true表示是官方图片
		    		  break;
		    	   }else{
		    		   i=i+6;
		    	   }
		       }
		       //break;
		    }
			/*while(iterator.hasNext()){
				System.out.println(iterator.next());
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}
		return dep;
	}
	
	public static List<String> filter2_1(String url){
		List<String> ls=new ArrayList<String>();
		List<String> result=new ArrayList<String>();
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter titleNF=new HasAttributeFilter("class","hd3");
			NodeList list=parser.extractAllNodesThatMatch(titleNF);
			Tag tag=(Tag)list.elementAt(0);
			String str=tag.toHtml();
			int i=str.indexOf("外观图片");
			if(i<0) return ls;
			else{
				int count=0,in=str.length()-1;
				while(count<4){
					in=str.lastIndexOf("href",in);
					//System.out.println(in);
					count++;
					in=in-2;
				}
				in=in+2;
				int j=str.indexOf('"',in+6);
				String tmp="http://db.auto.sohu.com"+str.substring(in+6,j);
				result.add(tmp);
				result.addAll(filter3_1(tmp,true));
				ls=filter4(result,true);
				//System.out.println(tmp);
			}
			//System.out.println(str);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ls;
	}
	
	public static List<String> filter3(String url){
		List<String> result=new ArrayList<String>();
		List<String> ls=new ArrayList<String>();
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter titleNF=new HasAttributeFilter("class","hd3");
			NodeList list=parser.extractAllNodesThatMatch(titleNF);
			Tag tag=(Tag)list.elementAt(0);
			String str=tag.toHtml();
			//System.out.println(str);
			int count=0;int i=0;
			while(count<2){
				i=str.indexOf("href",i);
				if(i<0) break;
				count++;
				i=i+6;
			}
			int j=str.indexOf('"',i);
			String tmp=str.substring(i,j);
			tmp="http://db.auto.sohu.com"+tmp;
			result.add(tmp);
			result.addAll(filter3_1(tmp,false));
			/*Iterator iterator=result.iterator();
			while(iterator.hasNext()){
				System.out.println(iterator.next());
			}*/
			ls=filter4(result,false);
			//System.out.println(tmp);
			//result=filter4(tmp);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return ls;
	}
	
	public static List<String> filter3_1(String url,boolean gf){
		List<String> ls=new ArrayList<String>();
		
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","back");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    while(list.size()!=0){
		       //System.out.println(list.size());
		       Tag tag=(Tag)list.elementAt(0);
		       String str=tag.toHtml();
		       int i=str.indexOf("href");
		       int j=str.indexOf('"',i+6);
		       String tmp="http://db.auto.sohu.com"+str.substring(i+6,j);
		       //System.out.println(tmp);
		        ls.add(tmp);
		       
		        Parser parser1=new Parser();
				parser1.setEncoding(parser1.getEncoding());
			    URL urls1 = new URL(tmp);
			    //System.out.println(tmp);
			    HttpURLConnection connection1 = (HttpURLConnection)urls1.openConnection();     
			    connection1.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
			    parser1.setConnection(connection1);
		        //System.out.println(tmp);
			    classNF=new HasAttributeFilter("class","back");
			    list=parser1.extractAllNodesThatMatch(classNF);
		    }
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		return ls;
	}
	
	public static List<String> filter4(List<String> result,boolean gf){
		List<String> ls=new ArrayList<String>();
		List<String> ls1=new ArrayList<String>();
		try{
			Iterator iterator=result.iterator();
			while(iterator.hasNext()){
			   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       URL urls = new URL(iterator.next().toString());
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		       parser.setConnection(connection);
		    
		       NodeFilter divNF=new HasAttributeFilter("class","bd");
		       NodeList list=parser.extractAllNodesThatMatch(divNF);
		       Tag tag=(Tag)list.elementAt(0);
		       String str=tag.toHtml();
		       int i=0;
		       boolean even=false;
		       while(i<str.length()){
		    	   i=str.indexOf("href",i);
		    	   if(i<0) break;
		    	   int j=str.indexOf('"',i+6);
		    	   //System.out.println(str.substring(i+6,j));
		    	   if(even==false){
		    	      ls1.add("http://db.auto.sohu.com"+str.substring(i+6,j));
		    	      even=true;
		    	   }else{
		    		   even=false;
		    	   }
		    	   i=i+6;
		       }
		       
		       //ls1.addAll(filter4_1(url));
		       /*Iterator iterator1=ls1.iterator();
		       while(iterator1.hasNext()){
		    	   System.out.println(iterator1.next());
		       }*/
		       //System.out.println(list.size());
		       
		       //System.out.println(iterator.next());
			}
			//System.out.println(ls1.size());
			ls=filter5(ls1,gf);
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		return ls;
	}
	
	public static List<String> filter5(List<String> ls1,boolean gf){
		List<String> ls=new ArrayList<String>();
		try{
			Iterator iterator=ls1.iterator();
			while(iterator.hasNext()){
			   String url=iterator.next().toString();
			   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       URL urls = new URL(url);
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		       parser.setConnection(connection);
		       
		       NodeFilter imgNF=new HasAttributeFilter("class","pic_img");
		       NodeList list=parser.extractAllNodesThatMatch(imgNF);
		       //System.out.println(list.size());
		       Tag tag=(Tag)list.elementAt(0);
		       String str=tag.toHtml();
		       int i=str.indexOf("src");
		       int j=str.indexOf('"',i+5);
		       ls.add(str.substring(i+5,j));
			}
			int number=1;
			if(!gf){
			   index++;
			   numberall=ls.size();
			}else{
			   number=numberall+1;
			   numberall=0;
			}
			Iterator iterator1=ls.iterator();
			//System.out.println(ls.size());
	        while(iterator1.hasNext()){
	        	//System.out.println(iterator1.next());
	            downloadImg(iterator1.next().toString(),"d:/"+String.valueOf(index),String.valueOf(number)+".jpg");
	            number++;
	        }
			System.out.println("下载成功");
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		//List<String> l=new ArrayList<String>();
		return ls;
	}
	
    public static void downloadImg(String imgSrc,String filePath,String fileName){
    	try{
    		URL url = new URL(imgSrc);
    		URLConnection conn = url.openConnection();
    		conn.setConnectTimeout(3*1000); 
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
        String  url="http://db.auto.sohu.com/cxdata/";
        ParseCar.filter(url);
	}
}
