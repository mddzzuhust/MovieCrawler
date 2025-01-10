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
public class ParseXCar {
    static int num=0;
	/*public static void filter(String url){
		String str="http://newcar.xcar.com.cn";
		List<String> ls=new ArrayList<String>();
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
			URL urls=new URL(url);
			HttpURLConnection connection=(HttpURLConnection)urls.openConnection();
			parser.setConnection(connection);
			
			NodeFilter ClassNF=new HasAttributeFilter("class","fra_box_lt");
			NodeList list=parser.extractAllNodesThatMatch(ClassNF);
			Tag tag=(Tag)list.elementAt(0);
			String string=tag.toHtml();
			//System.out.println(string);
			int index=0;
			while(true){
				index=string.indexOf("href",index);
				if(index<0) break;
				int last=string.indexOf('"',index+6);
				String tmp=string.substring(index+6, last);
				tmp=str+tmp;
				//System.out.println(tmp);
				ls.add(tmp);
				index=index+6;
			}
			filter2(ls);
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	public static void filter2(String url){
		String str="http://newcar.xcar.com.cn";
		List<String> lst=new ArrayList<String>();
		List<String> lstname=new ArrayList<String>();
		try{
			//Iterator iterator=ls.iterator();
			//while(iterator.hasNext()){
			   //String url=iterator.next().toString();
			   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
			   URL urls=new URL(url);
			   HttpURLConnection connection=(HttpURLConnection)urls.openConnection();
			   parser.setConnection(connection);
			   
			   NodeFilter classNF=new HasAttributeFilter("class","brand-tit");
			   NodeList list=parser.extractAllNodesThatMatch(classNF);
			   Tag tag=(Tag)list.elementAt(0);
			   String string=tag.toPlainTextString();
			   System.out.println(string);
			   //makefile(string);
			   //建立文件夹
			   parser.reset();
			   classNF=new HasAttributeFilter("class","name");
			   list=parser.extractAllNodesThatMatch(classNF);
			   //System.out.println(list.size());
			   
			   for(int num=1;num<list.size();num++){
				   tag=(Tag)list.elementAt(num);
				   //System.out.println(tag.toHtml().toString());
			       //break;
				   String s=tag.toHtml().toString();
				   int iofh=s.indexOf("href");
				   int lofh=s.indexOf('"',iofh+6);
				   String tmp=s.substring(iofh+6, lofh);
				   tmp=str+tmp;
				   //System.out.println(tmp);
				   lst.add(tmp);
				   
				   int ioft=s.indexOf("title");
				   int loft=s.indexOf('"',ioft+7);
				   String filename2=s.substring(ioft+7,loft);
				   filename2=filename2.replaceAll("\\s*", "");
				   filename2=filename2.replaceAll(":","");
				   //System.out.println(filename2+"a");
				   lstname.add(filename2);
				   //makefile(string,filename2);
			   }
			   filter3(lst,lstname,string);
			//}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter3(List<String> lst,List<String> lstname,String filename){
		String str="http://newcar.xcar.com.cn";
		try{
			Iterator iterator=lst.iterator();
			Iterator iterator1=lstname.iterator();
			while(iterator.hasNext()&&iterator1.hasNext()){
			   String url=iterator.next().toString();
			   String filename2=iterator1.next().toString();
			   //System.out.println(filename2);
			   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
			   URL urls=new URL(url);
			   HttpURLConnection connection=(HttpURLConnection)urls.openConnection();
			   parser.setConnection(connection);
			   
			   NodeFilter classNF=new HasAttributeFilter("title","整体外观");
			   NodeList list=parser.extractAllNodesThatMatch(classNF);
			   //System.out.println(list.size()+"madongdong");
			   if(list.size()<1) return;
			   Tag tag=(Tag)list.elementAt(0);
			   String string=tag.toHtml().toString();
			   //System.out.println(string);
			   int index=string.indexOf("href");
			   if(index<0) return;
			   int last=string.indexOf('"',index+6);
			   String  tmp=string.substring(index+6,last);
			   tmp=str+tmp;
			   //System.out.println(tmp);
			   filter4(tmp,filename2,filename);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter4(String url,String filename2,String filename){
		String str="http://newcar.xcar.com.cn";
		List<String> ls=new ArrayList<String>();
		List<String> lstname=new ArrayList<String>();
		try{
			Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
			URL urls=new URL(url);
			HttpURLConnection connection = (HttpURLConnection)urls.openConnection();
			parser.setConnection(connection);
			
			NodeFilter classNF=new HasAttributeFilter("class","color_item");
			NodeList list=parser.extractAllNodesThatMatch(classNF);
			if(list.size()<1) {
				//makefile(filename,filename2,"nocolor");
				ls.add(url);
				lstname.add("nocolor");
				filter5(ls,lstname,filename2,filename);
				return;
			}
			//System.out.println(list.size());
			for(int num=0;num<list.size();num++){
				Tag tag=(Tag)list.elementAt(num);
				//System.out.println(tag.toHtml());
				String string=tag.toHtml().toString();
				int index=string.indexOf("href");
				int last=string.indexOf('"',index+6);
				String tmp=string.substring(index+6,last);
				tmp=str+tmp;
				//System.out.println(tmp);
				ls.add(tmp);
				//break;
				
				int ioft=string.indexOf("title");
				int loft=string.indexOf('"',ioft+7);
				String filename3=string.substring(ioft+7,loft);
				filename3=filename3.replaceAll("\\s*", "");
				lstname.add(filename3);
				//makefile(filename,filename2,filename3);
			}
			filter5(ls,lstname,filename2,filename);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter5(List<String> ls,List<String>lstname,String filename2,String filename){
	    String str="http://newcar.xcar.com.cn";
		List<String> lst=new ArrayList<String>();
	    try{
			Iterator iterator=ls.iterator();
			Iterator iterator1=lstname.iterator();
			String[] URLs=new String[ls.size()];
			String[] filenames=new String[lstname.size()];
			for(int i=0;i<ls.size()&&i<lstname.size();i++){
				URLs[i]=iterator.next().toString();
				//System.out.println(URLs[i]);
				filenames[i]=iterator1.next().toString();
				//System.out.println(filenames[i]);
			}
			//System.out.println(ls.size());
			int order=0;
			while(order<URLs.length){
			   //String url=iterator.next().toString();
			   //System.out.println(URLs[order]);
			   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
			   URL urls=new URL(URLs[order]);
			   HttpURLConnection connection = (HttpURLConnection)urls.openConnection();
			   parser.setConnection(connection);
			   
			   NodeFilter classNF=new HasAttributeFilter("class","pic-con");
			   NodeList list=parser.extractAllNodesThatMatch(classNF);
			   //System.out.println(list.size());
			   //if(list.size()<1) filter6(ls);
			   Tag tag=(Tag)list.elementAt(0);
			   String string=tag.toHtml().toString();
			   //System.out.println(string);
			   int index=0;
			   boolean flag=true;
			   while(true){
				   index=string.indexOf("href",index);
				   if(index<0) break;
				   if(flag==false){
					   index=index+6;
					   flag=true;
					   continue;
				   }
				   int last=string.indexOf('"',index+6);
				   String tmp=string.substring(index+6,last);
				   tmp=str+tmp;
				   //System.out.println(tmp);
				   lst.add(tmp);
				   flag=false;
				   index=index+6;
			   }
			   filter6(lst,filenames[order],filename2,filename);
			   lst.clear();
			   order++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter6(List<String> ls,String filename3,String filename2,String filename){
		//String str="http://newcar.xcar.com.cn";
		List<String> lst=new ArrayList<String>();
		List<String> lstname=new ArrayList<String>();
		try{
			Iterator iterator=ls.iterator();
			String[] URLs=new String[ls.size()];
			for(int i=0;i<ls.size();i++){
				URLs[i]=iterator.next().toString();
				//System.out.println(URLs[i]);
			}
			int order=0;
			while(order<URLs.length){
			   //System.out.println(URLs[order]);
			   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
			   URL urls=new URL(URLs[order]);
			   HttpURLConnection connection = (HttpURLConnection)urls.openConnection();
			   parser.setConnection(connection);
			   order++;
			   
			   NodeFilter classNF=new HasAttributeFilter("class","current_img");
			   NodeList list=parser.extractAllNodesThatMatch(classNF);
			   //System.out.println(list.size());
			   Tag tag=(Tag)list.elementAt(0);
			   String string=tag.toHtml().toString();
			   int index=string.indexOf("src=",0);
			   int last=string.indexOf('"',index+5);
			   String tmp=string.substring(index+5,last);
			   //System.out.println(tmp);
			   lst.add(tmp);
			   
			   int ioft=string.indexOf("title");
			   int loft=string.indexOf('款',ioft+7);
			   String filename4=string.substring(ioft+7,loft+1);
			   filename4=filename4.replaceAll("\\s*", "");
			   //makefile(filename,filename2,filename3,filename4);
			   lstname.add(filename4);
			}
			predownloadImg(lst,lstname,filename3,filename2,filename);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void predownloadImg(List<String> ls,List<String> lstname,String filename3,String filename2,String filename){
		Iterator iterator=ls.iterator();
		Iterator iterator1=lstname.iterator();
		while(iterator.hasNext()&&iterator1.hasNext()){
			String filename4=iterator1.next().toString();
			//System.out.println(filename4);
			String filePath=makefile(filename,filename2,filename3,filename4);
		    System.out.println(filePath);
			downloadImg(iterator.next().toString(),filePath,String.valueOf(++num)+".jpg");
			System.out.println("下载成功");
		}
	}
	
	public static String makefile(String filename,String filename2,String filename3,String filename4){
		File file=new File("E:/Car/"+filename);
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		file=new File("E:/Car/"+filename+"/"+filename2);
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		file=new File("E:/Car/"+filename+"/"+filename2+"/"+filename3+"/");
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		file=new File("E:/Car/"+filename+"/"+filename2+"/"+filename3+"/"+filename4);
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
			return file.toString();
		}
		return file.toString();
		//System.out.println("创建成功");
	}
	
	public static void downloadImg(String imgSrc,String filePath,String fileName){
    	try{
    		URL url = new URL(imgSrc);
    		URLConnection conn = url.openConnection();
    		conn.setConnectTimeout(3000*1000); 
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
        String[] url=new String[15];
        url[0]="http://newcar.xcar.com.cn/photo/pb147/";
        url[1]="http://newcar.xcar.com.cn/photo/pb151/";
        url[2]="http://newcar.xcar.com.cn/photo/pb110/";
        url[3]="http://newcar.xcar.com.cn/photo/pb142/";
        url[4]="http://newcar.xcar.com.cn/photo/pb122/";
        url[5]="http://newcar.xcar.com.cn/photo/pb161/";
        url[6]="http://newcar.xcar.com.cn/photo/pb167/";
        url[7]="http://newcar.xcar.com.cn/photo/pb9/";
        url[8]="http://newcar.xcar.com.cn/photo/pb82/";
        url[9]="http://newcar.xcar.com.cn/photo/pb98/";
        url[10]="http://newcar.xcar.com.cn/photo/pb145/";
        url[11]="http://newcar.xcar.com.cn/photo/pb128/";
        url[12]="http://newcar.xcar.com.cn/photo/pb137/";
        url[13]="http://newcar.xcar.com.cn/photo/pb182/";
        url[14]="http://newcar.xcar.com.cn/photo/pb187/";
        for(int i=0;i<url.length;i++)
              filter2(url[i]);
		//makefile("奥迪","奥迪A3三厢","冰川白","2015款");
	}

}
