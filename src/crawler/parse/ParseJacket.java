package crawler.parse;

import java.net.HttpURLConnection;
import java.net.URL;

import org.htmlparser.Parser;

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

public class ParseJacket {
	static int number=0;
    public static void filter(String url){
    	try{
    		 List<String> al=new ArrayList<String>();
    	     Parser parser=new Parser();
     	     parser.setEncoding(parser.getEncoding());
     	     URL urls = new URL(url);
 		     HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
 		     connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
 		     parser.setConnection(connection);
 		     
 		     NodeFilter nf=new HasAttributeFilter("class","productImg-wrap");
 		     NodeList list=parser.extractAllNodesThatMatch(nf);
 		     for(int i=0;i<list.size();i++){
 		    	 Tag tag=(Tag)list.elementAt(i);
 		    	 String string=tag.toHtml().toString();
 		    	 int index=string.indexOf("href");
 		    	 int last=string.indexOf('"',index+6);
 		    	 al.add("https:"+string.substring(index+6, last));
 		     }
 		     filter2(al);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void filter2(List<String> al){
    	try{
    		Iterator iterator=al.iterator();
    		while(iterator.hasNext()){
    			  String url=iterator.next().toString();
    			  Parser parser=new Parser();
    	    	  parser.setEncoding(parser.getEncoding());
    	    	  URL urls = new URL(url);
    			  HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
    			  connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
    			  parser.setConnection(connection);
    			
    			  NodeFilter nf=new HasAttributeFilter("id","J_ImgBooth");
    			  NodeList list=parser.extractAllNodesThatMatch(nf);
    			  Tag tag=(Tag)list.elementAt(0);
    			  String img=tag.toHtml().toString();
    			  int index=img.indexOf("src");
    			  int last=img.indexOf('"',index+5);
    			  //System.out.println(img.substring(index+5,last));
    			  download("https:"+img.substring(index+5,last),String.valueOf(++number)+".jpg");
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
    		File saveDir=new File("E:\\pic\\men\\shoes\\wangmianxie");
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
         ParseJacket pj=new ParseJacket();
         String[] urls=new String[10];
         urls[0]="https://list.tmall.com/search_product.htm?spm=a221t.7059849.navcat2.18.5Lglzj&abbucket=&cat=53846003&sort=s&acm=lb-zebra-22355-327956.1003.8.454870&aldid=226900&from=sn_1_cat&pos=18&style=g&search_condition=55&industryCatId=50106419&active=1&abtest=&pic_detail=1&scm=1003.8.lb-zebra-22355-327956.ITEM_144346517499918_454870#J_crumbs&theme=437";
         urls[1]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.10.Z9I7rb&cat=53846003&s=60&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         urls[2]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.CAEyDs&cat=53846003&s=120&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         urls[3]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.TkA2na&cat=53846003&s=180&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         urls[4]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.cUrw7D&cat=53846003&s=240&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         urls[5]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.SHMLPY&cat=53846003&s=300&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         urls[6]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.ZceyOY&cat=53846003&s=360&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         urls[7]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.PUK1WR&cat=53846003&s=420&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         urls[8]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.tiILWM&cat=53846003&s=480&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         urls[9]="https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.11.0PXuLb&cat=53846003&s=540&sort=s&style=g&search_condition=55&pic_detail=1&from=sn_1_cat&active=1&industryCatId=50106419&tmhkmain=0&type=pc#J_Filter";
         for(int i=0;i<urls.length;i++){
            pj.filter(urls[i]);
         }
     }
}
