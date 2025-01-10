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

public class ParseXuan {
	static int number=0;
    public static void filter(String url){
    	//System.out.println(url);
    	String str="http://data.chooseauto.com.cn/";
    	List<String> ls=new ArrayList<String>();
    	List<String> filename2s=new ArrayList<String>();
    	try{
    		Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("class","rm_cx");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    for(int i=0;i<list.size();i++){
		       Tag tag=(Tag)list.elementAt(i);
		       String string=tag.toHtml().toString();
		       int index=0;
		       int count=0;
		       while(true){
		    	   index=string.indexOf("href=",index);
		    	   if(index<0) break;
		    	   if(count==1){
		    		   index=index+6;
		    		   count++;
		    		   continue;
		    	   }else if(count==2){
		    		   index=index+6;
		    		   count=0;
		    		   continue;
		    	   }
		    	   int last=string.indexOf("\"",index+6);
		    	   String tmp=string.substring(index+6,last);
		    	   tmp=str+tmp;
		    	   ls.add(tmp);
		    	   //System.out.println(tmp);
		    	   index=index+6;
		    	   count++;
		      }
		    }
		    //System.out.println(ls.size());
		    parser.reset();
		    classNF=new HasAttributeFilter("class","tk_title");
		    list=parser.extractAllNodesThatMatch(classNF);
		    Tag tag=(Tag)list.elementAt(0);
		    String string=tag.toHtml().toString();
		    int last=string.lastIndexOf("</a></ul>");
		    int index=string.lastIndexOf("target=\"_blank\">",last);
		    String filename1=string.substring(index+16, last);
		    //System.out.println(filename1);
		    
		    parser.reset();
		    classNF=new HasAttributeFilter("class","blue_color");
		    list=parser.extractAllNodesThatMatch(classNF);
		    for(int i=0;i<list.size();i++){
		       tag=(Tag)list.elementAt(i);
		       string=tag.toHtml().toString();
		       //System.out.println(string);
		       index=string.indexOf("blue_color");
		       last=string.indexOf("</a>");
		       String filename2=string.substring(index+12,last);
		       //System.out.println(filename2);
		       filename2s.add(filename2);
		    }
		    filter2(ls,filename2s,filename1);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter2(List<String> ls,List<String> filename2s,String filename1){
    	String str="http://data.chooseauto.com.cn/";
    	List<String> l=new ArrayList<String>();
    	Iterator iterator=ls.iterator();
    	Iterator iterator2=filename2s.iterator();
    	List<String> filename22=new ArrayList<String>();
    	//System.out.println(filename2s.size());
    	try{
    		while(iterator.hasNext()&&iterator2.hasNext()){
    			  String url=iterator.next().toString();
    			  String filename2=iterator2.next().toString();
         		  //System.out.println(url);
         		  //System.out.println(filename2);
         		  Parser parser=new Parser();
     			  parser.setEncoding(parser.getEncoding());
     		      URL urls = new URL(url);
     		      HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
     		      parser.setConnection(connection);
     		      
     		      NodeFilter classNF=new HasAttributeFilter("class","pic_fl");
     		      NodeList list=parser.extractAllNodesThatMatch(classNF);
     		      if(list.size()<1) continue;
     		      Tag tag=(Tag)list.elementAt(0);
     		      String string=tag.toHtml().toString();
     		      
     		      int index=string.indexOf("href");
     		      if(index<0) continue;
     		      int last=string.indexOf("\"",index+6);
     		      l.add(str+string.substring(index+6,last));
     		      
     		      filename22.add((filename2));
     		      //System.out.println(filename2);
     		      //System.out.println(str+string.substring(index+6,last));
    		}
    		//System.out.println("aaa");
    		filter3(l,filename22,filename1);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter3(List<String> l,List<String> filename22,String filename1){
    	String str="http://data.chooseauto.com.cn/";
    	List<String> ls=new ArrayList<String>();
    	Iterator iterator=l.iterator();
    	Iterator iterator2=filename22.iterator();
    	//System.out.println(l.size());
    	while(iterator.hasNext()&&iterator2.hasNext()){
    		try{
    			ls.clear();
    			String url=iterator.next().toString();
  			    String filename2=iterator2.next().toString();
  			    System.out.println(url);
  			    //System.out.println(url);
       		    //System.out.println(filename2);
       		    Parser parser=new Parser();
   			    parser.setEncoding(parser.getEncoding());
   		        URL urls = new URL(url);
   		        HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
   		        parser.setConnection(connection);
   		        
   		        NodeFilter classNF=new HasAttributeFilter("class","xl_img");
   		        NodeList list=parser.extractAllNodesThatMatch(classNF);
   		        if(list.size()<1) continue;
   		        Tag tag=(Tag)list.elementAt(0);
   		        String string=tag.toHtml().toString();
   		        int index=0;
   		        while(true){
   		        	index=string.indexOf("href",index);
   		        	if(index<0) break;
   		        	int last=string.indexOf("\"",index+6);
   		        	String tmp=str+string.substring(index+6,last);
   		        	ls.add(tmp);
   		        	//System.out.println(tmp);
   		        	index=index+6;
   		        }
   		        System.out.println(ls.size());
   		        filter4(ls,filename1,filename2);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void filter4(List<String> ls,String filename1,String filename2){
    	
    	String str="http://data.chooseauto.com.cn";
        Iterator iterator=ls.iterator();
    	try{
    		while(iterator.hasNext()){
    			String url=iterator.next().toString();
    			Parser parser=new Parser();
    			parser.setEncoding(parser.getEncoding());
    		    URL urls = new URL(url);
    		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
    		    parser.setConnection(connection);
    		    
    		    NodeFilter classNF=new HasAttributeFilter("id","v_img");
    		    NodeList list=parser.extractAllNodesThatMatch(classNF);
    		    if(list.size()<1) continue;
    		    Tag tag=(Tag)list.elementAt(0);
    		    String string=tag.toHtml().toString();
    		    
    		    int index=string.indexOf("src=");
    		    int last=string.indexOf("\"",index+5);
    		    String pic=str+string.substring(index+5,last);
    		    predownloadImg(pic,filename1,filename2);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void predownloadImg(String url,String filename1,String filename2){
    	String filePath=makefile(filename1,filename2);
    	downloadImg(url,filePath,String.valueOf(++number)+".jpg");
    	System.out.println("Yeah!"+number);
    }
    
    public static String makefile(String filename1,String filename2){
    	filename1=filename1.replaceAll(" ","");
    	filename2=filename2.replaceAll(" ","");
    	File file=new File("D:/Car/"+filename1);
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		file=new File("D:/Car/"+filename1+"/"+filename2);
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		file=new File("D:/Car/"+filename1+"/"+filename2+"/");
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
        String[] urls=new String[70];
        urls[0]="http://data.chooseauto.com.cn/soueast/tp.shtml";
        urls[1]="http://data.chooseauto.com.cn/volkswagen/tp.shtml";
        urls[2]="http://data.chooseauto.com.cn/dfmc/tp.shtml";
        urls[3]="http://data.chooseauto.com.cn/dodge/tp.shtml";
        urls[4]="http://data.chooseauto.com.cn/DS/tp.shtml";
        urls[5]="http://data.chooseauto.com.cn/futian/tp.shtml";
        urls[6]="http://data.chooseauto.com.cn/fiat/tp.shtml";
        urls[7]="http://data.chooseauto.com.cn/ford/tp.shtml";
        urls[8]="http://data.chooseauto.com.cn/toyota/tp.shtml";
        urls[9]="http://data.chooseauto.com.cn/gonow/tp.shtml";
        urls[10]="http://data.chooseauto.com.cn/guangqi/tp.shtml";
        urls[11]="http://data.chooseauto.com.cn/qoros/tp.shtml";
        urls[12]="http://data.chooseauto.com.cn/hongqi/tp.shtml";
        urls[13]="http://data.chooseauto.com.cn/haval/tp.shtml";
        urls[14]="http://data.chooseauto.com.cn/huanghai/tp.shtml";
        urls[15]="http://data.chooseauto.com.cn/haima/tp.shtml";
        urls[16]="http://data.chooseauto.com.cn/hafei/tp.shtml";
        urls[17]="http://data.chooseauto.com.cn/hawtai/tp.shtml";
        urls[18]="http://data.chooseauto.com.cn/geely/tp.shtml";
        urls[19]="http://data.chooseauto.com.cn/jeep/tp.shtml";
        urls[20]="http://data.chooseauto.com.cn/jmc/tp.shtml";
        urls[21]="http://data.chooseauto.com.cn/jac/tp.shtml";
        urls[22]="http://data.chooseauto.com.cn/jinbei/tp.shtml";
        urls[23]="http://data.chooseauto.com.cn/jaguar/tp.shtml";
        urls[24]="http://data.chooseauto.com.cn/chrysler/tp.shtml";
        urls[25]="http://data.chooseauto.com.cn/cadillac/tp.shtml";
        urls[26]="http://data.chooseauto.com.cn/karry/tp.shtml";
        urls[27]="http://data.chooseauto.com.cn/renault/tp.shtml";
        urls[28]="http://data.chooseauto.com.cn/lifan/tp.shtml";
        urls[29]="http://data.chooseauto.com.cn/landrover/tp.shtml";
        urls[30]="http://data.chooseauto.com.cn/lexus/tp.shtml";
        urls[31]="http://data.chooseauto.com.cn/lamborghini/tp.shtml";
        urls[32]="http://data.chooseauto.com.cn/changfeng/tp.shtml";
        urls[33]="http://data.chooseauto.com.cn/suzuki/tp.shtml";
        urls[34]="http://data.chooseauto.com.cn/lincoln/tp.shtml";
        urls[35]="http://data.chooseauto.com.cn/everus/tp.shtml";
        urls[36]="http://data.chooseauto.com.cn/landwind/tp.shtml";
        urls[37]="http://data.chooseauto.com.cn/lotus/tp.shtml";
        urls[38]="http://data.chooseauto.com.cn/mini/tp.shtml";
        urls[39]="http://data.chooseauto.com.cn/McLaren/tp.shtml";
        urls[40]="http://data.chooseauto.com.cn/mg/tp.shtml";
        urls[41]="http://data.chooseauto.com.cn/mazda/tp.shtml";
        urls[42]="http://data.chooseauto.com.cn/maserati/tp.shtml";
        urls[43]="http://data.chooseauto.com.cn/Luxgen/tp.shtml";
        urls[44]="http://data.chooseauto.com.cn/acura/tp.shtml";
        urls[45]="http://data.chooseauto.com.cn/younglotus/tp.shtml";
        urls[46]="http://data.chooseauto.com.cn/kia/tp.shtml";
        urls[47]="http://data.chooseauto.com.cn/chery/tp.shtml";
        urls[48]="http://data.chooseauto.com.cn/venucia/tp.shtml";
        urls[49]="http://data.chooseauto.com.cn/nissan/tp.shtml";
        urls[50]="http://data.chooseauto.com.cn/riich/tp.shtml";
        urls[51]="http://data.chooseauto.com.cn/roewe/tp.shtml";
        urls[52]="http://data.chooseauto.com.cn/sb/tp.shtml";
        urls[53]="http://data.chooseauto.com.cn/smart/tp.shtml";
        urls[54]="http://data.chooseauto.com.cn/mitsubishi/tp.shtml";
        urls[55]="http://data.chooseauto.com.cn/subaru/tp.shtml";
        urls[56]="http://data.chooseauto.com.cn/ssangyong/tp.shtml";
        urls[57]="http://data.chooseauto.com.cn/skoda/tp.shtml";
        urls[58]="http://data.chooseauto.com.cn/volvo/tp.shtml";
        urls[59]="http://data.chooseauto.com.cn/rely/tp.shtml";
        urls[60]="http://data.chooseauto.com.cn/hyundai/tp.shtml";
        urls[61]="http://data.chooseauto.com.cn/citroen/tp.shtml";
        urls[62]="http://data.chooseauto.com.cn/chevrolet/tp.shtml";
        urls[63]="http://data.chooseauto.com.cn/seat/tp.shtml";
        urls[64]="http://data.chooseauto.com.cn/jonway/tp.shtml";
        urls[65]="http://data.chooseauto.com.cn/yema/tp.shtml";
        urls[66]="http://data.chooseauto.com.cn/faw/tp.shtml";
        urls[67]="http://data.chooseauto.com.cn/infiniti/tp.shtml";
        urls[68]="http://data.chooseauto.com.cn/zotye/tp.shtml";
        urls[69]="http://data.chooseauto.com.cn/zhonghua/tp.shtml";
        for(int i=0;i<urls.length;i++)
            ParseXuan.filter(urls[i]);
        
        //自己定义数组爬的快一点
	}

}
