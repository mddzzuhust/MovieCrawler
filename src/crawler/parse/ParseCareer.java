package crawler.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;

public class ParseCareer {
	static int count=0;
    public static void filter1(String url){
    	List<String> ls=new ArrayList<String>();
    	try{
    		Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter AttrNF=new HasAttributeFilter("class","productImg-wrap");
		    NodeList list=parser.extractAllNodesThatMatch(AttrNF);
		    for(int i=0;i<list.size();i++){
		    	Tag tag=(Tag)list.elementAt(i);
		    	String string=tag.toHtml();
		    	int index=string.indexOf("href");
		    	int last=string.indexOf("\"", index+6);
		    	//System.out.println(string.substring(index+6,last));
		    	ls.add("https:"+string.substring(index+6,last));
		    }
		    //System.out.println(list.size());
		    filter2(ls);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter2(List<String> ls){
       Iterator iterator=ls.iterator();
       try{
    	   count=230;
    	   while(iterator.hasNext()){
    		   String url=iterator.next().toString();
    		   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       URL urls = new URL(url);
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		       parser.setConnection(connection);
		       
			   NodeFilter AttrNF=new HasAttributeFilter("class","tb-booth");
			   NodeList list=parser.extractAllNodesThatMatch(AttrNF);
			   Tag tag=(Tag)list.elementAt(0);
			   String string=tag.toHtml();
			   //System.out.println(string);
			   int index=string.indexOf("src=");
			   int last=string.indexOf("\"",index+5);
			   String img="https:"+string.substring(index+5,last);
			   //System.out.println(img);
			   
			   count++;
			   downloadImg(img,"C:/Users/mdd/Desktop/特种职业/保安",String.valueOf(count)+".jpg");
			   System.out.println("下载成功");
    	   }
       }catch(Exception e){
    	   e.printStackTrace();
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
        ParseCareer p=new ParseCareer();
        p.filter1("https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.9D5fYf&s=180&q=%B1%A3%B0%B2&sort=s&style=g&from=.list.pc_1_searchbutton&type=pc#J_Filter");
	}

}
