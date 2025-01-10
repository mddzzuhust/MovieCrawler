package crawler.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Iterator;
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

public class ParseCar3 {
	static int count=0;
    static String str="http://car.autohome.com.cn";
	public static void filter(String url,int round){
		List<String> ls=new ArrayList<String>();
		ls.add(url);
		String Str=null;
		try{
			   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       URL urls = new URL(url);
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		       parser.setConnection(connection);
		       
		       NodeFilter classNF=new HasAttributeFilter("class","fn-left cartab-title-name");
		       //NodeFilter classnf=new HasAttributeFilter("class","mark");
		       //NodeFilter andNF=new AndFilter(new NodeFilter[]{classNF,classnf});
		       NodeList list=parser.extractAllNodesThatMatch(classNF);
		       if(list.size()>0){
		       Tag tag=(Tag)list.elementAt(0);
		       String string=tag.toHtml();
		       //System.out.println(string);
		       int i=string.indexOf("</a>");
		       int j=string.lastIndexOf('>',i);
		       Str=string.substring(j+1,i);
		       //System.out.println(Str);
		       }
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			String s=url;
			boolean flag=false;
			while(true){
			   //System.out.println(s);
			   Parser parser=new Parser();
			   parser.setEncoding(parser.getEncoding());
		       URL urls = new URL(s);
		       HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		       connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		       parser.setConnection(connection);
		    
		       NodeFilter classNF=new HasAttributeFilter("class","page-item-next");
		       NodeList list=parser.extractAllNodesThatMatch(classNF);
		       //System.out.println(list.size());
		       if(list.size()<1) break;
		       Tag tag=(Tag)list.elementAt(0);
		       //System.out.println(tag.toHtml());
		       String string=tag.toHtml();
		       int i=string.indexOf("下一页");
		       if(i<0)  break;
		       i=string.lastIndexOf("href",i);
		       int j=string.indexOf('"',i+6);
		       string=string.substring(i+6,j);
		       //System.out.println(string);
		       s=str+string;
		       ls.add(s);
		       //System.out.println(s);
		    }
			filter2(ls,Str,round);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter2(List<String> ls,String name,int round){
		List<String> lst=new ArrayList<String>();
		try{
			Iterator iterator=ls.iterator();
			int count=0;
			while(iterator.hasNext()){
				String s=iterator.next().toString();
				Parser parser=new Parser();
				parser.setEncoding(parser.getEncoding());
			    URL urls = new URL(s);
			    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
			    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
			    parser.setConnection(connection);
			    
			    NodeFilter classNF=new HasAttributeFilter("class","uibox-con carpic-list03 border-b-solid");
			    NodeList list=parser.extractAllNodesThatMatch(classNF);
			    Tag tag=(Tag)list.elementAt(0);
			    String string=tag.toHtml();
			    int i=0;
			    boolean flag=true;
			    while(i<string.length()){
			    	i=string.indexOf("href",i);
			    	if(i<0) break;
			    	if(flag==false){
			    		i=i+6;
			    		flag=true;
			    		continue;
			    	}
			    	int j=string.indexOf('"',i+6);
			    	String tmp=string.substring(i+6,j);
			    	tmp=str+tmp;
			    	//System.out.println(tmp+"         "+(++count));
			    	lst.add(tmp);
			    	i=i+6;
			    	flag=false;
			    }
			}
			filter3(lst,name,round);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter3(List<String> ls,String name,int round){
		List<String> lst=new ArrayList<String>();
		try{
			Iterator iterator=ls.iterator();
			if((round-1)%3==0)
				count=0;
			while(iterator.hasNext()){
				String s=iterator.next().toString();
				Parser parser=new Parser();
				parser.setEncoding(parser.getEncoding());
			    URL urls = new URL(s);
			    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
			    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
			    parser.setConnection(connection);
			    
			    NodeFilter classNF=new HasAttributeFilter("id","img");
			    NodeList list=parser.extractAllNodesThatMatch(classNF);
			    Tag tag=(Tag)list.elementAt(0);
			    String string=tag.toHtml();
			    int i=string.indexOf("src");
			    int j=string.indexOf('"',i+5);
			    String tmp=string.substring(i+5,j);
			    count++;
			    //System.out.println(name+"     "+tmp+"   "+count);
			    downloadImg(tmp,"d:/image5/"+name,String.valueOf(count)+".jpg");
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
		String[]  string=new String[300];
        string[0]="http://car.autohome.com.cn/pic/series/2139-12.html";
        string[1]="http://car.autohome.com.cn/pic/series/2139-200.html";
        string[2]="http://car.autohome.com.cn/pic/series/2139-14.html";
        string[3]="http://car.autohome.com.cn/pic/series/526-12.html";
        string[4]="http://car.autohome.com.cn/pic/series/526-200.html";
        string[5]="http://car.autohome.com.cn/pic/series/526-14.html";
        string[6]="http://car.autohome.com.cn/pic/series/614-12.html";
        string[7]="http://car.autohome.com.cn/pic/series/614-200.html";
        string[8]="http://car.autohome.com.cn/pic/series/614-14.html";
        string[9]="http://car.autohome.com.cn/pic/series/2123-12.html";
        string[10]="http://car.autohome.com.cn/pic/series/2123-200.html";
        string[11]="http://car.autohome.com.cn/pic/series/2123-14.html";
        string[12]="http://car.autohome.com.cn/pic/series/3412-12.html";
        string[13]="http://car.autohome.com.cn/pic/series/3412-200.html";
        string[14]="http://car.autohome.com.cn/pic/series/3412-14.html";
        string[15]="http://car.autohome.com.cn/pic/series/448-12.html";
        string[16]="http://car.autohome.com.cn/pic/series/448-200.html";
        string[17]="http://car.autohome.com.cn/pic/series/448-14.html";
        string[18]="http://car.autohome.com.cn/pic/series/874-12.html";
        string[19]="http://car.autohome.com.cn/pic/series/874-200.html";
        string[20]="http://car.autohome.com.cn/pic/series/874-14.html";
        string[21]="http://car.autohome.com.cn/pic/series/442-12.html";
        string[22]="http://car.autohome.com.cn/pic/series/442-200.html";
        string[23]="http://car.autohome.com.cn/pic/series/442-14.html";
        string[24]="http://car.autohome.com.cn/pic/series/982-12.html";
        string[25]="http://car.autohome.com.cn/pic/series/982-200.html";
        string[26]="http://car.autohome.com.cn/pic/series/982-14.html";
        string[27]="http://car.autohome.com.cn/pic/series/16-12.html";
        string[28]="http://car.autohome.com.cn/pic/series/16-200.html";
        string[29]="http://car.autohome.com.cn/pic/series/16-14.html";
        string[30]="http://car.autohome.com.cn/pic/series/2451-12.html";
        string[31]="http://car.autohome.com.cn/pic/series/2451-200.html";
        string[32]="http://car.autohome.com.cn/pic/series/2451-14.html";
        string[33]="http://car.autohome.com.cn/pic/series/3657-12.html";
        string[34]="http://car.autohome.com.cn/pic/series/3657-200.html";
        string[35]="http://car.autohome.com.cn/pic/series/3657-14.html";
        string[36]="http://car.autohome.com.cn/pic/series/871-12.html";
        string[37]="http://car.autohome.com.cn/pic/series/871-200.html";
        string[38]="http://car.autohome.com.cn/pic/series/871-14.html";
        string[39]="http://car.autohome.com.cn/pic/series/111-12.html";
        string[40]="http://car.autohome.com.cn/pic/series/111-200.html";
        string[41]="http://car.autohome.com.cn/pic/series/111-14.html";
        string[42]="http://car.autohome.com.cn/pic/series/65-12.html";
        string[43]="http://car.autohome.com.cn/pic/series/65-200.html";
        string[44]="http://car.autohome.com.cn/pic/series/65-14.html";
        string[45]="http://car.autohome.com.cn/pic/series/2456-12.html";
        string[46]="http://car.autohome.com.cn/pic/series/2456-14.html";
        string[47]="http://car.autohome.com.cn/pic/series/2456-200.html";
        string[48]="http://car.autohome.com.cn/pic/series/314-12.html";
        string[49]="http://car.autohome.com.cn/pic/series/314-14.html";
        string[50]="http://car.autohome.com.cn/pic/series/314-200.html";
        string[51]="http://car.autohome.com.cn/pic/series/3080-12.html";
        string[52]="http://car.autohome.com.cn/pic/series/3080-14.html";
        string[53]="http://car.autohome.com.cn/pic/series/3080-200.html";
        string[54]="http://car.autohome.com.cn/pic/series/2922-12.html";
        string[55]="http://car.autohome.com.cn/pic/series/2922-14.html";
        string[56]="http://car.autohome.com.cn/pic/series/2922-200.html";
        string[57]="http://car.autohome.com.cn/pic/series/770-12.html";
        string[58]="http://car.autohome.com.cn/pic/series/770-14.html";
        string[59]="http://car.autohome.com.cn/pic/series/770-200.html";
        string[60]="http://car.autohome.com.cn/pic/series/3347-12.html";
        string[61]="http://car.autohome.com.cn/pic/series/3347-14.html";
        string[62]="http://car.autohome.com.cn/pic/series/3347-200.html";
        string[63]="http://car.autohome.com.cn/pic/series/657-12.html";
        string[64]="http://car.autohome.com.cn/pic/series/657-14.html";
        string[65]="http://car.autohome.com.cn/pic/series/657-200.html";
        string[66]="http://car.autohome.com.cn/pic/series/3556-12.html";
        string[67]="http://car.autohome.com.cn/pic/series/3556-14.html";
        string[68]="http://car.autohome.com.cn/pic/series/3556-200.html";
        string[69]="http://car.autohome.com.cn/pic/series/3204-12.html";
        string[70]="http://car.autohome.com.cn/pic/series/3204-14.html";
        string[71]="http://car.autohome.com.cn/pic/series/3204-200.html";
        string[72]="http://car.autohome.com.cn/pic/series/2562-12.html";
        string[73]="http://car.autohome.com.cn/pic/series/2562-14.html";
        string[74]="http://car.autohome.com.cn/pic/series/2562-200.html";
        string[75]="http://car.autohome.com.cn/pic/series/3691-12.html";
        string[76]="http://car.autohome.com.cn/pic/series/3691-14.html";
        string[77]="http://car.autohome.com.cn/pic/series/3691-200.html";
        string[78]="http://car.autohome.com.cn/pic/series/656-12.html";
        string[79]="http://car.autohome.com.cn/pic/series/656-14.html";
        string[80]="http://car.autohome.com.cn/pic/series/656-200.html";
        string[81]="http://car.autohome.com.cn/pic/series/2764-12.html";
        string[82]="http://car.autohome.com.cn/pic/series/2764-14.html";
        string[83]="http://car.autohome.com.cn/pic/series/2764-200.html";
        string[84]="http://car.autohome.com.cn/pic/series/2319-12.html";
        string[85]="http://car.autohome.com.cn/pic/series/2319-14.html";
        string[86]="http://car.autohome.com.cn/pic/series/2319-200.html";
        string[87]="http://car.autohome.com.cn/pic/series/633-12.html";
        string[88]="http://car.autohome.com.cn/pic/series/633-14.html";
        string[89]="http://car.autohome.com.cn/pic/series/633-200.html";
        string[90]="http://car.autohome.com.cn/pic/series/3554-12.html";
        string[91]="http://car.autohome.com.cn/pic/series/3554-14.html";
        string[92]="http://car.autohome.com.cn/pic/series/3554-200.html";
        string[93]="http://car.autohome.com.cn/pic/series/2429-12.html";
        string[94]="http://car.autohome.com.cn/pic/series/2429-14.html";
        string[95]="http://car.autohome.com.cn/pic/series/2429-200.html";
        string[96]="http://car.autohome.com.cn/pic/series/3582-12.html";
        string[97]="http://car.autohome.com.cn/pic/series/3582-14.html";
        string[98]="http://car.autohome.com.cn/pic/series/3582-200.html";
        string[99]="http://car.autohome.com.cn/pic/series/875-12.html";
        string[100]="http://car.autohome.com.cn/pic/series/875-200.html";
        string[101]="http://car.autohome.com.cn/pic/series/875-3.html";
        string[102]="http://car.autohome.com.cn/pic/series/3460-12.html";
        string[103]="http://car.autohome.com.cn/pic/series/3460-14.html";
        string[104]="http://car.autohome.com.cn/pic/series/3460-200.html";
        string[105]="http://car.autohome.com.cn/pic/series/81-12.html";
        string[106]="http://car.autohome.com.cn/pic/series/81-14.html";
        string[107]="http://car.autohome.com.cn/pic/series/81-200.html";
        string[108]="http://car.autohome.com.cn/pic/series/3073-12.html";
        string[109]="http://car.autohome.com.cn/pic/series/3073-14.html";
        string[110]="http://car.autohome.com.cn/pic/series/3073-200.html";
        string[111]="http://car.autohome.com.cn/pic/series/2615-12.html";
        string[112]="http://car.autohome.com.cn/pic/series/2615-14.html";
        string[113]="http://car.autohome.com.cn/pic/series/2615-200.html";
        string[114]="http://car.autohome.com.cn/pic/series/519-12.html";
        string[115]="http://car.autohome.com.cn/pic/series/519-14.html";
        string[116]="http://car.autohome.com.cn/pic/series/519-200.html";
        string[117]="http://car.autohome.com.cn/pic/series/2778-12.html";
        string[118]="http://car.autohome.com.cn/pic/series/2778-14.html";
        string[119]="http://car.autohome.com.cn/pic/series/2778-200.html";
        string[120]="http://car.autohome.com.cn/pic/series/3462-12.html";
        string[121]="http://car.autohome.com.cn/pic/series/3462-14.html";
        string[122]="http://car.autohome.com.cn/pic/series/3462-200.html";
        string[123]="http://car.autohome.com.cn/pic/series/145-12.html";
        string[124]="http://car.autohome.com.cn/pic/series/145-14.html";
        string[125]="http://car.autohome.com.cn/pic/series/145-200.html";
        string[126]="http://car.autohome.com.cn/pic/series/3677-12.html";
        string[127]="http://car.autohome.com.cn/pic/series/3677-14.html";
        string[128]="http://car.autohome.com.cn/pic/series/3677-200.html";
        string[129]="http://car.autohome.com.cn/pic/series/117-12.html";
        string[130]="http://car.autohome.com.cn/pic/series/117-14.html";
        string[131]="http://car.autohome.com.cn/pic/series/117-200.html";
        string[132]="http://car.autohome.com.cn/pic/series/496-12.html";
        string[133]="http://car.autohome.com.cn/pic/series/496-14.html";
        string[134]="http://car.autohome.com.cn/pic/series/496-200.html";
        string[135]="http://car.autohome.com.cn/pic/series/163-12.html";
        string[136]="http://car.autohome.com.cn/pic/series/163-14.html";
        string[137]="http://car.autohome.com.cn/pic/series/163-200.html";
        string[138]="http://car.autohome.com.cn/pic/series/110-12.html";
        string[139]="http://car.autohome.com.cn/pic/series/110-14.html";
        string[140]="http://car.autohome.com.cn/pic/series/110-200.html";
        string[141]="http://car.autohome.com.cn/pic/series/18-12.html";
        string[142]="http://car.autohome.com.cn/pic/series/18-14.html";
        string[143]="http://car.autohome.com.cn/pic/series/18-200.html";
        string[144]="http://car.autohome.com.cn/pic/series/634-12.html";
        string[145]="http://car.autohome.com.cn/pic/series/634-14.html";
        string[146]="http://car.autohome.com.cn/pic/series/634-200.html";
        string[147]="http://car.autohome.com.cn/pic/series/50-12.html";
        string[148]="http://car.autohome.com.cn/pic/series/50-3.html";
        string[149]="http://car.autohome.com.cn/pic/series/50-10.html";
        string[150]="http://car.autohome.com.cn/pic/series/692-12.html";
        string[151]="http://car.autohome.com.cn/pic/series/692-14.html";
        string[152]="http://car.autohome.com.cn/pic/series/692-200.html";
        string[153]="http://car.autohome.com.cn/pic/series/771-12.html";
        string[154]="http://car.autohome.com.cn/pic/series/771-14.html";
        string[155]="http://car.autohome.com.cn/pic/series/771-200.html";
        string[156]="http://car.autohome.com.cn/pic/series/3397-12.html";
        string[157]="http://car.autohome.com.cn/pic/series/3397-14.html";
        string[158]="http://car.autohome.com.cn/pic/series/3397-200.html";
        string[159]="http://car.autohome.com.cn/pic/series/3422-12.html";
        string[160]="http://car.autohome.com.cn/pic/series/3422-14.html";
        string[161]="http://car.autohome.com.cn/pic/series/3422-200.html";
        string[162]="http://car.autohome.com.cn/pic/series/78-12.html";
        string[163]="http://car.autohome.com.cn/pic/series/78-51.html";
        string[164]="http://car.autohome.com.cn/pic/series/78-200.html";
        string[165]="http://car.autohome.com.cn/pic/series/2334-12.html";
        string[166]="http://car.autohome.com.cn/pic/series/2334-14.html";
        string[167]="http://car.autohome.com.cn/pic/series/2334-200.html";
        string[168]="http://car.autohome.com.cn/pic/series/2863-12.html";
        string[169]="http://car.autohome.com.cn/pic/series/2863-14.html";
        string[170]="http://car.autohome.com.cn/pic/series/2863-200.html";
        string[171]="http://car.autohome.com.cn/pic/series/987-12.html";
        string[172]="http://car.autohome.com.cn/pic/series/987-14.html";
        string[173]="http://car.autohome.com.cn/pic/series/987-200.html";
        string[174]="http://car.autohome.com.cn/pic/series/3292-12.html";
        string[175]="http://car.autohome.com.cn/pic/series/3292-14.html";
        string[176]="http://car.autohome.com.cn/pic/series/3292-200.html";
        string[177]="http://car.autohome.com.cn/pic/series/3615-12.html";
        string[178]="http://car.autohome.com.cn/pic/series/3615-14.html";
        string[179]="http://car.autohome.com.cn/pic/series/3615-200.html";
        string[180]="http://car.autohome.com.cn/pic/series/364-12.html";
        string[181]="http://car.autohome.com.cn/pic/series/364-14.html";
        string[182]="http://car.autohome.com.cn/pic/series/364-200.html";
        string[183]="http://car.autohome.com.cn/pic/series/3059-12.html";
        string[184]="http://car.autohome.com.cn/pic/series/3059-14.html";
        string[185]="http://car.autohome.com.cn/pic/series/3059-200.html";
        string[186]="http://car.autohome.com.cn/pic/series/812-12.html";
        string[187]="http://car.autohome.com.cn/pic/series/812-14.html";
        string[188]="http://car.autohome.com.cn/pic/series/812-200.html";
        string[189]="http://car.autohome.com.cn/pic/series/2115-12.html";
        string[190]="http://car.autohome.com.cn/pic/series/2115-14.html";
        string[191]="http://car.autohome.com.cn/pic/series/2115-200.html";
        string[192]="http://car.autohome.com.cn/pic/series/3294-12.html";
        string[193]="http://car.autohome.com.cn/pic/series/3294-14.html";
        string[194]="http://car.autohome.com.cn/pic/series/3294-200.html";
        string[195]="http://car.autohome.com.cn/pic/series/588-12.html";
        string[196]="http://car.autohome.com.cn/pic/series/588-14.html";
        string[197]="http://car.autohome.com.cn/pic/series/588-200.html";
        string[198]="http://car.autohome.com.cn/pic/series/164-12.html";
        string[199]="http://car.autohome.com.cn/pic/series/164-14.html";
        string[200]="http://car.autohome.com.cn/pic/series/164-200.html";
        string[201]="http://car.autohome.com.cn/pic/series/3530-12.html";
        string[202]="http://car.autohome.com.cn/pic/series/3530-14.html";
        string[203]="http://car.autohome.com.cn/pic/series/3530-200.html";
        string[204]="http://car.autohome.com.cn/pic/series/364-13.html";
        string[205]="http://car.autohome.com.cn/pic/series/364-51.html";
        string[206]="http://car.autohome.com.cn/pic/series/364-15.html";
        string[207]="http://car.autohome.com.cn/pic/series/66-12.html";
        string[208]="http://car.autohome.com.cn/pic/series/66-14.html";
        string[209]="http://car.autohome.com.cn/pic/series/66-200.html";
        string[210]="http://car.autohome.com.cn/pic/series/407-12.html";
        string[211]="http://car.autohome.com.cn/pic/series/407-14.html";
        string[212]="http://car.autohome.com.cn/pic/series/407-200.html";
        string[213]="http://car.autohome.com.cn/pic/series/2540-12.html";
        string[214]="http://car.autohome.com.cn/pic/series/2540-14.html";
        string[215]="http://car.autohome.com.cn/pic/series/2540-200.html";
        string[216]="http://car.autohome.com.cn/pic/series/3085-12.html";
        string[217]="http://car.autohome.com.cn/pic/series/3085-200.html";
        string[218]="http://car.autohome.com.cn/pic/series/3085-3.html";
        string[219]="http://car.autohome.com.cn/pic/series/3457-12.html";
        string[220]="http://car.autohome.com.cn/pic/series/3457-14.html";
        string[221]="http://car.autohome.com.cn/pic/series/3457-200.html";
        string[222]="http://car.autohome.com.cn/pic/series/166-12.html";
        string[223]="http://car.autohome.com.cn/pic/series/166-14.html";
        string[224]="http://car.autohome.com.cn/pic/series/166-200.html";
        string[225]="http://car.autohome.com.cn/pic/series/474-12.html";
        string[226]="http://car.autohome.com.cn/pic/series/474-14.html";
        string[227]="http://car.autohome.com.cn/pic/series/474-200.html";
        string[228]="http://car.autohome.com.cn/pic/series/528-12.html";
        string[229]="http://car.autohome.com.cn/pic/series/528-14.html";
        string[230]="http://car.autohome.com.cn/pic/series/528-200.html";
        string[231]="http://car.autohome.com.cn/pic/series/3126-12.html";
        string[232]="http://car.autohome.com.cn/pic/series/3126-14.html";
        string[233]="http://car.autohome.com.cn/pic/series/3126-200.html";
        string[234]="http://car.autohome.com.cn/pic/series/3103-12.html";
        string[235]="http://car.autohome.com.cn/pic/series/3103-14.html";
        string[236]="http://car.autohome.com.cn/pic/series/3103-200.html";
        string[237]="http://car.autohome.com.cn/pic/series/2566-12.html";
        string[238]="http://car.autohome.com.cn/pic/series/2566-14.html";
        string[239]="http://car.autohome.com.cn/pic/series/2566-200.html";
        string[240]="http://car.autohome.com.cn/pic/series/64-12.html";
        string[241]="http://car.autohome.com.cn/pic/series/64-14.html";
        string[242]="http://car.autohome.com.cn/pic/series/64-200.html";
        string[243]="http://car.autohome.com.cn/pic/series/2886-12.html";
        string[244]="http://car.autohome.com.cn/pic/series/2886-15.html";
        string[245]="http://car.autohome.com.cn/pic/series/2886-200.html";
        string[246]="http://car.autohome.com.cn/pic/series/2605-12.html";
        string[247]="http://car.autohome.com.cn/pic/series/2605-14.html";
        string[248]="http://car.autohome.com.cn/pic/series/2605-200.html";
        string[249]="http://car.autohome.com.cn/pic/series/2619-12.html";
        string[250]="http://car.autohome.com.cn/pic/series/2619-14.html";
        string[251]="http://car.autohome.com.cn/pic/series/2619-200.html";
        string[252]="http://car.autohome.com.cn/pic/series/197-12.html";
        string[253]="http://car.autohome.com.cn/pic/series/197-14.html";
        string[254]="http://car.autohome.com.cn/pic/series/197-200.html";
        string[255]="http://car.autohome.com.cn/pic/series/2951-12.html";
        string[256]="http://car.autohome.com.cn/pic/series/2951-14.html";
        string[257]="http://car.autohome.com.cn/pic/series/2951-200.html";
        string[258]="http://car.autohome.com.cn/pic/series/564-12.html";
        string[259]="http://car.autohome.com.cn/pic/series/564-14.html";
        string[260]="http://car.autohome.com.cn/pic/series/564-200.html";
        string[261]="http://car.autohome.com.cn/pic/series/3195-12.html";
        string[262]="http://car.autohome.com.cn/pic/series/3195-14.html";
        string[263]="http://car.autohome.com.cn/pic/series/3195-200.html";
        string[264]="http://car.autohome.com.cn/pic/series/2896-12.html";
        string[265]="http://car.autohome.com.cn/pic/series/2896-14.html";
        string[266]="http://car.autohome.com.cn/pic/series/2896-200.html";
        string[267]="http://car.autohome.com.cn/pic/series/2503-12.html";
        string[268]="http://car.autohome.com.cn/pic/series/2503-14.html";
        string[269]="http://car.autohome.com.cn/pic/series/2503-3.html";
        string[270]="http://car.autohome.com.cn/pic/series/3429-12.html";
        string[271]="http://car.autohome.com.cn/pic/series/3429-14.html";
        string[272]="http://car.autohome.com.cn/pic/series/3429-200.html";
        string[273]="http://car.autohome.com.cn/pic/series/3170-12.html";
        string[274]="http://car.autohome.com.cn/pic/series/3170-14.html";
        string[275]="http://car.autohome.com.cn/pic/series/3170-200.html";
        string[276]="http://car.autohome.com.cn/pic/series/632-12.html";
        string[277]="http://car.autohome.com.cn/pic/series/632-14.html";
        string[278]="http://car.autohome.com.cn/pic/series/632-200.html";
        string[279]="http://car.autohome.com.cn/pic/series/3414-12.html";
        string[280]="http://car.autohome.com.cn/pic/series/3414-14.html";
        string[281]="http://car.autohome.com.cn/pic/series/3414-200.html";
        string[282]="http://car.autohome.com.cn/pic/series/2987-12.html";
        string[283]="http://car.autohome.com.cn/pic/series/2987-14.html";
        string[284]="http://car.autohome.com.cn/pic/series/2987-200.html";
        string[285]="http://car.autohome.com.cn/pic/series/877-12.html";
        string[286]="http://car.autohome.com.cn/pic/series/877-200.html";
        string[287]="http://car.autohome.com.cn/pic/series/877-3.html";
        string[288]="http://car.autohome.com.cn/pic/series/2062-12.html";
        string[289]="http://car.autohome.com.cn/pic/series/2062-14.html";
        string[290]="http://car.autohome.com.cn/pic/series/2062-200.html";
        string[291]="http://car.autohome.com.cn/pic/series/2761-12.html";
        string[292]="http://car.autohome.com.cn/pic/series/2761-14.html";
        string[293]="http://car.autohome.com.cn/pic/series/2761-200.html";
        string[294]="http://car.autohome.com.cn/pic/series/2949-12.html";
        string[295]="http://car.autohome.com.cn/pic/series/2949-14.html";
        string[296]="http://car.autohome.com.cn/pic/series/2949-200.html";
        string[297]="http://car.autohome.com.cn/pic/series/3104-12.html";
        string[298]="http://car.autohome.com.cn/pic/series/3104-14.html";
        string[299]="http://car.autohome.com.cn/pic/series/3104-200.html";
        for(int i=0;i<string.length;i++){
        	System.out.println(i+1);
        	filter(string[i],i+1);
        }
	}

}
