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
public class ParseTai {
	static int number=0;
	
	public static void filter(String url){
		List<String> l=new ArrayList<String>();
		String str="http://price.pcauto.com.cn";
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","ulPic ulPic-b clearfix");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    
		    for(int i=0;i<list.size();i++){
		    	Tag tag=(Tag)list.elementAt(i);
		    	String txt=tag.toHtml().toString();
		    	int index=0;
		    	while(true){
		    		index=txt.indexOf("href",index);
		    		if(index<0) break;
		    		int last=txt.indexOf("\"",index+6);
		    		String tmp=txt.substring(index+6,last);
		    		//tmp=str+tmp;
		    		l.add(tmp);
		    		System.out.println(tmp);
		    		index=index+6;
		    	}
		    }
		    
		    parser.reset();
		    classNF=new HasAttributeFilter("class","mark");
		    list=parser.extractAllNodesThatMatch(classNF);
		    Tag tag=(Tag)list.elementAt(0);
		    String tmp=tag.toHtml().toString();
		    int last=tmp.lastIndexOf("</a>");
		    int index=tmp.lastIndexOf(">",last);
		    String filename1=tmp.substring(index+1,last);
		    //System.out.println(filename1);
		    filter2(l,filename1);
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	public static void filter2(List<String> ls,String filename1){
		String str="http://price.pcauto.com.cn";
    	Iterator iterator=ls.iterator();
    	//System.out.println(filename1);
    	try{
    	   while(iterator.hasNext()){
    		  String url=iterator.next().toString();
       		  //System.out.println(url);
       		  Parser parser=new Parser();
   			  parser.setEncoding(parser.getEncoding());
   		      URL urls = new URL(url);
   		      HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
   		      parser.setConnection(connection);
   		    
   		      NodeFilter classNF=new HasAttributeFilter("class","class-item fl clearfix");
		      NodeList list=parser.extractAllNodesThatMatch(classNF);
		      if(list.size()<1) continue;
		      Tag tag=(Tag)list.elementAt(0);
			  String string=tag.toHtml().toString();
			  int index=string.indexOf("<dd><a href=");
			  index=string.indexOf("<dd><a href=",index+13);
			  int last=string.indexOf("\"",index+13);
			  String tmp=/*str+*/string.substring(index+13,last);
		      //System.out.println(tmp);
			  
			  parser.reset();
			  classNF=new HasAttributeFilter("class","mark");
			  list=parser.extractAllNodesThatMatch(classNF);
			  if(list.size()<1) continue;
			  tag=(Tag)list.elementAt(0);
			  string=tag.toHtml().toString();
			  last=string.lastIndexOf("</a>");
			  index=string.lastIndexOf(">",last);
			  String filename2=string.substring(index+1,last);
			  //System.out.println(filename2);
			  //break;
			  filter3(tmp,filename1,filename2);
    	   }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
	public static void filter3(String url,String filename1,String filename2){
		String str="http://price.pcauto.com.cn";
		List<String> ls=new ArrayList<String>();
    	ls.add(url);
    	System.out.println(filename1+"  "+filename2);
    	try{
    		Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","next");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    //System.out.println("Yeah!");
		    if(list.size()==0){
		    	filter4(ls,filename1,filename2);
		    	return;
		    }else{
		    	 while(list.size()>0){
			    	Tag tag=(Tag)list.elementAt(0);
			    	String string=tag.toHtml().toString();
			    	int index=string.indexOf("href");
			    	int last=string.indexOf("\"",index+6);
			    	String tmp=/*str+*/string.substring(index+6,last);
			    	//System.out.println(tmp);
			    	ls.add(tmp);
			    	
			    	parser.reset();
			    	parser.setEncoding(parser.getEncoding());
				    urls = new URL(tmp);
				    connection = (HttpURLConnection)urls.openConnection();     
				    parser.setConnection(connection);
				    classNF=new HasAttributeFilter("class","next");
				    list=parser.extractAllNodesThatMatch(classNF);
		    	 }
		    	 filter4(ls,filename1,filename2);
		    }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
	public static void filter4(List<String>ls,String filename1,String filename2){
		String str="http://price.pcauto.com.cn";
		List<String> l=new ArrayList<String>();
    	List<String> lfilename3=new ArrayList<String>();
    	String filename3;
    	Iterator iterator=ls.iterator();
    	try{
    		while(iterator.hasNext()){
    			String url=iterator.next().toString();
    	    	Parser parser=new Parser();
    			parser.setEncoding(parser.getEncoding());
    		    URL urls = new URL(url);
    		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
    		    parser.setConnection(connection);
    		    
    		    NodeFilter classNF=new HasAttributeFilter("class","ulPic ulPic-180 clearfix");
    		    NodeList list=parser.extractAllNodesThatMatch(classNF);
    		    Tag tag=(Tag)list.elementAt(0);
    		    String string=tag.toHtml().toString();
    		    int index=0;
    		    while(true){
    		    	index=string.indexOf("href",index);
    		    	if(index<0) break;
    		    	int last=string.indexOf("\"",index+6);
    		    	String tmp=/*str+*/string.substring(index+6,last);
    		    	l.add(tmp);
    		    	int ioft=string.indexOf("title=",index);
    		    	int eoft=string.indexOf("\"",ioft+7);
    		        tmp=string.substring(ioft+7,eoft);
    		        if((tmp.charAt(0)-'2')!=0){
    		        	filename3=tmp;
    		        }else{
    		        	filename3=tmp.substring(0,5);
    		        }
    		        lfilename3.add(filename3);
    		        //System.out.println(filename3);
    		    	index=index+6;
    		    }
    		}
    		filter5(l,filename1,filename2,lfilename3);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
    public static void filter5(List<String> ls,String filename1,String filename2,List<String> lfilename3){
    	Iterator iterator=ls.iterator();
    	Iterator iterator1=lfilename3.iterator();
    	try{
    		while(iterator.hasNext()){
    			String url=iterator.next().toString();
    			String filename3=iterator1.next().toString();
    			Parser parser=new Parser();
    			parser.setEncoding(parser.getEncoding());
    		    URL urls = new URL(url);
    		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
    		    parser.setConnection(connection);
    		    
    		    NodeFilter classNF=new HasAttributeFilter("id","pic_img");
    		    NodeList list=parser.extractAllNodesThatMatch(classNF);
    		    if(list.size()<1) continue;
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
        String url="http://price.pcauto.com.cn/cars/nb693/";
        ParseTai.filter(url);
	}

}
