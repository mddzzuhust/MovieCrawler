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

public class ParseCar4 {
	static int count=0;
    static String str="http://pic.cheshi.com";
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
		       
		       NodeFilter classNF=new HasAttributeFilter("class","r");
		       //NodeFilter classnf=new HasAttributeFilter("class","mark");
		       //NodeFilter andNF=new AndFilter(new NodeFilter[]{classNF,classnf});
		       NodeList list=parser.extractAllNodesThatMatch(classNF);
		       if(list.size()>0){
		       Tag tag=(Tag)list.elementAt(0);
		       String string=tag.toHtml();
		       //System.out.println(string);
		       int i=string.indexOf("频道");
		       int j=string.lastIndexOf('>',i);
		       Str=string.substring(j+1,i);
		       Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		       Matcher m = p.matcher(Str);
		       Str=m.replaceAll("");
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
		    
		       NodeFilter classNF=new HasAttributeFilter("class","fip-over mb10");
		       NodeList list=parser.extractAllNodesThatMatch(classNF);
		       //System.out.println(list.size());
		       if(list.size()<1) break;
		       Tag tag=(Tag)list.elementAt(0);
		       //System.out.println(tag.toHtml());
		       String string=tag.toHtml();
		       int i=string.indexOf("下一页");
		       if(i<0)  break;
		       i=string.lastIndexOf("href",i);
		       //int j=string.indexOf('"',i+6);
		       int j=string.indexOf("html",i);
		       //System.out.println(k);
		       string=string.substring(i+6,j+4);
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
			    
			    NodeFilter classNF=new HasAttributeFilter("class","mPhoto clearfix ");
			    NodeList list=parser.extractAllNodesThatMatch(classNF);
			    Tag tag=(Tag)list.elementAt(0);
			    String string=tag.toHtml();
			    //System.out.println(string);
			    int i=0;
			    //boolean flag=true;
			    while(i<string.length()){
			    	i=string.indexOf("href",i);
			    	if(i<0) break;
			    	/*if(flag==false){
			    		i=i+6;
			    		flag=true;
			    		continue;
			    	}*/
			    	int j=string.indexOf('"',i+6);
			    	String tmp=string.substring(i+6,j);
			    	tmp=str+tmp;
			    	//System.out.println(tmp+"         "+(++count));
			    	lst.add(tmp);
			    	i=i+6;
			    	//flag=false;
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
			if((round-1)%2==0) count=0;
			while(iterator.hasNext()){
				String s=iterator.next().toString();
				Parser parser=new Parser();
				parser.setEncoding(parser.getEncoding());
			    URL urls = new URL(s);
			    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
			    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
			    parser.setConnection(connection);
			    
			    NodeFilter classNF=new HasAttributeFilter("class","pic");
			    NodeList list=parser.extractAllNodesThatMatch(classNF);
			    Tag tag=(Tag)list.elementAt(0);
			    String string=tag.toHtml();
			    int i=string.indexOf("src");
			    int j=string.indexOf('"',i+5);
			    String tmp=string.substring(i+5,j);
			    count++;
			    //System.out.println(name+"     "+tmp+"   "+count);
			    downloadImg(tmp,"d:/image6/"+name,String.valueOf(count)+".jpg");
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
		String[]  string=new String[200];
        string[0]="http://pic.cheshi.com/prdcategory_1247_t6.html";
        string[1]="http://pic.cheshi.com/prdcategory_1247_t2.html";
        string[2]="http://pic.cheshi.com/prdcategory_485_t6.html";
        string[3]="http://pic.cheshi.com/prdcategory_485_t2.html";
        string[4]="http://pic.cheshi.com/prdcategory_380_t6.html";
        string[5]="http://pic.cheshi.com/prdcategory_380_t2.html";
        string[6]="http://pic.cheshi.com/prdcategory_1259_t6.html";
        string[7]="http://pic.cheshi.com/prdcategory_1259_t2.html";
        string[8]="http://pic.cheshi.com/prdcategory_2307_t6.html";
        string[9]="http://pic.cheshi.com/prdcategory_2307_t2.html";
        string[10]="http://pic.cheshi.com/prdcategory_93_t6.html";
        string[11]="http://pic.cheshi.com/prdcategory_93_t2.html";
        string[12]="http://pic.cheshi.com/prdcategory_379_t6.html";
        string[13]="http://pic.cheshi.com/prdcategory_379_t2.html";
        string[14]="http://pic.cheshi.com/prdcategory_97_t6.html";
        string[15]="http://pic.cheshi.com/prdcategory_97_t2.html";
        string[16]="http://pic.cheshi.com/prdcategory_1252_t6.html";
        string[17]="http://pic.cheshi.com/prdcategory_1252_t2.html";
        string[18]="http://pic.cheshi.com/prdcategory_71_t6.html";
        string[19]="http://pic.cheshi.com/prdcategory_71_t2.html";
        string[20]="http://pic.cheshi.com/prdcategory_2463_t6.html";
        string[21]="http://pic.cheshi.com/prdcategory_2463_t2.html";
        string[22]="http://pic.cheshi.com/prdcategory_1098_t6.html";
        string[23]="http://pic.cheshi.com/prdcategory_1098_t2.html";
        string[24]="http://pic.cheshi.com/prdcategory_110_t6.html";
        string[25]="http://pic.cheshi.com/prdcategory_110_t2.html";
        string[26]="http://pic.cheshi.com/prdcategory_63_t6.html";
        string[27]="http://pic.cheshi.com/prdcategory_63_t2.html";
        string[28]="http://pic.cheshi.com/prdcategory_6_t6.html";
        string[29]="http://pic.cheshi.com/prdcategory_6_t2.html";
        string[30]="http://pic.cheshi.com/prdcategory_413_t6.html";
        string[31]="http://pic.cheshi.com/prdcategory_413_t2.html";
        string[32]="http://pic.cheshi.com/prdcategory_213_t6.html";
        string[33]="http://pic.cheshi.com/prdcategory_213_t2.html";
        string[34]="http://pic.cheshi.com/prdcategory_2398_t6.html";
        string[35]="http://pic.cheshi.com/prdcategory_2398_t2.html";
        string[36]="http://pic.cheshi.com/prdcategory_2619_t6.html";
        string[37]="http://pic.cheshi.com/prdcategory_2619_t2.html";
        string[38]="http://pic.cheshi.com/prdcategory_1031_t6.html";
        string[39]="http://pic.cheshi.com/prdcategory_1031_t2.html";
        string[40]="http://pic.cheshi.com/prdcategory_2036_t6.html";
        string[41]="http://pic.cheshi.com/prdcategory_2036_t2.html";
        string[42]="http://pic.cheshi.com/prdcategory_1026_t6.html";
        string[43]="http://pic.cheshi.com/prdcategory_1026_t2.html";
        string[44]="http://pic.cheshi.com/prdcategory_2410_t6.html";
        string[45]="http://pic.cheshi.com/prdcategory_2410_t2.html";
        string[46]="http://pic.cheshi.com/prdcategory_2161_t6.html";
        string[47]="http://pic.cheshi.com/prdcategory_2161_t2.html";
        string[48]="http://pic.cheshi.com/prdcategory_1641_t6.html";
        string[49]="http://pic.cheshi.com/prdcategory_1641_t2.html";
        string[50]="http://pic.cheshi.com/prdcategory_2552_t6.html";
        string[51]="http://pic.cheshi.com/prdcategory_2552_t1.html";
        string[52]="http://pic.cheshi.com/prdcategory_223_t6.html";
        string[53]="http://pic.cheshi.com/prdcategory_223_t2.html";
        string[54]="http://pic.cheshi.com/prdcategory_1814_t6.html";
        string[55]="http://pic.cheshi.com/prdcategory_1814_t2.html";
        string[56]="http://pic.cheshi.com/prdcategory_1540_t6.html";
        string[57]="http://pic.cheshi.com/prdcategory_1540_t2.html";
        string[58]="http://pic.cheshi.com/prdcategory_64_t6.html";
        string[59]="http://pic.cheshi.com/prdcategory_64_t2.html";
        string[60]="http://pic.cheshi.com/prdcategory_1545_t6.html";
        string[61]="http://pic.cheshi.com/prdcategory_1545_t2.html";
        string[62]="http://pic.cheshi.com/prdcategory_1521_t6.html";
        string[63]="http://pic.cheshi.com/prdcategory_1521_t2.html";
        string[64]="http://pic.cheshi.com/prdcategory_2428_t8.html";
        string[65]="http://pic.cheshi.com/prdcategory_2428_t1.html";
        string[66]="http://pic.cheshi.com/prdcategory_404_t6.html";
        string[67]="http://pic.cheshi.com/prdcategory_404_t2.html";
        string[68]="http://pic.cheshi.com/prdcategory_2331_t6.html";
        string[69]="http://pic.cheshi.com/prdcategory_2331_t2.html";
        string[70]="http://pic.cheshi.com/prdcategory_105_t6.html";
        string[71]="http://pic.cheshi.com/prdcategory_105_t2.html";
        string[72]="http://pic.cheshi.com/prdcategory_2088_t6.html";
        string[73]="http://pic.cheshi.com/prdcategory_2088_t2.html";
        string[74]="http://pic.cheshi.com/prdcategory_2043_t6.html";
        string[75]="http://pic.cheshi.com/prdcategory_2043_t2.html";
        string[76]="http://pic.cheshi.com/prdcategory_423_t6.html";
        string[77]="http://pic.cheshi.com/prdcategory_423_t2.html";        
        string[78]="http://pic.cheshi.com/prdcategory_1838_t6.html";
        string[79]="http://pic.cheshi.com/prdcategory_1838_t2.html";
        string[80]="http://pic.cheshi.com/prdcategory_2342_t6.html";
        string[81]="http://pic.cheshi.com/prdcategory_2342_t2.html";
        string[82]="http://pic.cheshi.com/prdcategory_375_t6.html";
        string[83]="http://pic.cheshi.com/prdcategory_375_t2.html";
        string[84]="http://pic.cheshi.com/prdcategory_2306_t6.html";
        string[85]="http://pic.cheshi.com/prdcategory_2306_t2.html";
        string[86]="http://pic.cheshi.com/prdcategory_90_t6.html";
        string[87]="http://pic.cheshi.com/prdcategory_90_t2.html";
        string[88]="http://pic.cheshi.com/prdcategory_478_t6.html";
        string[89]="http://pic.cheshi.com/prdcategory_478_t2.html";
        string[90]="http://pic.cheshi.com/prdcategory_406_t6.html";
        string[91]="http://pic.cheshi.com/prdcategory_406_t2.html";
        string[92]="http://pic.cheshi.com/prdcategory_45_t6.html";
        string[93]="http://pic.cheshi.com/prdcategory_45_t2.html";
        string[94]="http://pic.cheshi.com/prdcategory_11_t6.html";
        string[95]="http://pic.cheshi.com/prdcategory_11_t2.html";
        string[96]="http://pic.cheshi.com/prdcategory_56_t6.html";
        string[97]="http://pic.cheshi.com/prdcategory_56_t2.html";
        string[98]="http://pic.cheshi.com/prdcategory_1213_t6.html";
        string[99]="http://pic.cheshi.com/prdcategory_1213_t2.html";
        string[100]="http://pic.cheshi.com/prdcategory_1040_t6.html";
        string[101]="http://pic.cheshi.com/prdcategory_1040_t2.html";
        string[102]="http://pic.cheshi.com/prdcategory_1032_t6.html";
        string[103]="http://pic.cheshi.com/prdcategory_1032_t2.html";
        string[104]="http://pic.cheshi.com/prdcategory_81_t6.html";
        string[105]="http://pic.cheshi.com/prdcategory_81_t2.html";
        string[106]="http://pic.cheshi.com/prdcategory_2425_t6.html";
        string[107]="http://pic.cheshi.com/prdcategory_2425_t2.html";
        string[108]="http://pic.cheshi.com/prdcategory_100_t6.html";
        string[109]="http://pic.cheshi.com/prdcategory_100_t2.html";
        string[110]="http://pic.cheshi.com/prdcategory_1833_t6.html";
        string[111]="http://pic.cheshi.com/prdcategory_1833_t2.html";
        string[112]="http://pic.cheshi.com/prdcategory_1961_t6.html";
        string[113]="http://pic.cheshi.com/prdcategory_1961_t2.html";
        string[114]="http://pic.cheshi.com/prdcategory_1171_t6.html";
        string[115]="http://pic.cheshi.com/prdcategory_1171_t2.html";
        string[116]="http://pic.cheshi.com/prdcategory_2356_t6.html";
        string[117]="http://pic.cheshi.com/prdcategory_2356_t2.html";
        string[118]="http://pic.cheshi.com/prdcategory_52_t6.html";
        string[119]="http://pic.cheshi.com/prdcategory_52_t2.html";
        string[120]="http://pic.cheshi.com/prdcategory_84_t6.html";
        string[121]="http://pic.cheshi.com/prdcategory_84_t8.html";
        string[122]="http://pic.cheshi.com/prdcategory_2093_t6.html";
        string[123]="http://pic.cheshi.com/prdcategory_2093_t2.html";
        string[124]="http://pic.cheshi.com/prdcategory_1119_t6.html";
        string[125]="http://pic.cheshi.com/prdcategory_1119_t2.html";
        string[126]="http://pic.cheshi.com/prdcategory_1214_t6.html";
        string[127]="http://pic.cheshi.com/prdcategory_1214_t2.html";
        string[128]="http://pic.cheshi.com/prdcategory_2299_t6.html";
        string[129]="http://pic.cheshi.com/prdcategory_2299_t2.html";
        string[130]="http://pic.cheshi.com/prdcategory_113_t6.html";
        string[131]="http://pic.cheshi.com/prdcategory_113_t2.html";
        string[132]="http://pic.cheshi.com/prdcategory_49_t6.html";
        string[133]="http://pic.cheshi.com/prdcategory_49_t2.html";
        string[134]="http://pic.cheshi.com/prdcategory_1565_t6.html";
        string[135]="http://pic.cheshi.com/prdcategory_1565_t2.html";
        string[136]="http://pic.cheshi.com/prdcategory_1188_t6.html";
        string[137]="http://pic.cheshi.com/prdcategory_1188_t2.html";
        string[138]="http://pic.cheshi.com/prdcategory_4_t6.html";
        string[139]="http://pic.cheshi.com/prdcategory_4_t2.html";
        string[140]="http://pic.cheshi.com/prdcategory_18_t6.html";
        string[141]="http://pic.cheshi.com/prdcategory_18_t2.html";
        string[142]="http://pic.cheshi.com/prdcategory_104_t6.html";
        string[143]="http://pic.cheshi.com/prdcategory_104_t2.html";
        string[144]="http://pic.cheshi.com/prdcategory_2064_t6.html";
        string[145]="http://pic.cheshi.com/prdcategory_2064_t2.html";
        string[146]="http://pic.cheshi.com/prdcategory_2433_t6.html";
        string[147]="http://pic.cheshi.com/prdcategory_2433_t2.html";
        string[148]="http://pic.cheshi.com/prdcategory_399_t6.html";
        string[149]="http://pic.cheshi.com/prdcategory_399_t2.html";
        string[150]="http://pic.cheshi.com/prdcategory_2384_t6.html";
        string[151]="http://pic.cheshi.com/prdcategory_2384_t2.html";
        string[152]="http://pic.cheshi.com/prdcategory_1495_t6.html";
        string[153]="http://pic.cheshi.com/prdcategory_1495_t2.html";
        string[154]="http://pic.cheshi.com/prdcategory_2135_t6.html";
        string[155]="http://pic.cheshi.com/prdcategory_2135_t2.html";
        string[156]="http://pic.cheshi.com/prdcategory_2049_t6.html";
        string[157]="http://pic.cheshi.com/prdcategory_2049_t2.html";
        string[158]="http://pic.cheshi.com/prdcategory_146_t6.html";
        string[159]="http://pic.cheshi.com/prdcategory_146_t2.html";
        string[160]="http://pic.cheshi.com/prdcategory_1756_t6.html";
        string[161]="http://pic.cheshi.com/prdcategory_1756_t2.html";
        string[162]="http://pic.cheshi.com/prdcategory_218_t6.html";
        string[163]="http://pic.cheshi.com/prdcategory_218_t2.html";
        string[164]="http://pic.cheshi.com/prdcategory_1839_t6.html";
        string[165]="http://pic.cheshi.com/prdcategory_1839_t6.html";
        string[166]="http://pic.cheshi.com/prdcategory_1946_t6.html";
        string[167]="http://pic.cheshi.com/prdcategory_1946_t2.html";
        string[168]="http://pic.cheshi.com/prdcategory_17_t6.html";
        string[169]="http://pic.cheshi.com/prdcategory_17_t2.html";
        string[170]="http://pic.cheshi.com/prdcategory_1952_t6.html";
        string[171]="http://pic.cheshi.com/prdcategory_1952_t2.html";
        string[172]="http://pic.cheshi.com/prdcategory_222_t6.html";
        string[173]="http://pic.cheshi.com/prdcategory_222_t2.html";
        string[174]="http://pic.cheshi.com/prdcategory_355_t6.html";
        string[175]="http://pic.cheshi.com/prdcategory_355_t2.html";
        string[176]="http://pic.cheshi.com/prdcategory_1763_t6.html";
        string[177]="http://pic.cheshi.com/prdcategory_1763_t2.html";
        string[178]="http://pic.cheshi.com/prdcategory_1863_t4.html";
        string[179]="http://pic.cheshi.com/prdcategory_1863_t2.html";
        string[180]="http://pic.cheshi.com/prdcategory_2357_t6.html";
        string[181]="http://pic.cheshi.com/prdcategory_2357_t2.html";
        string[182]="http://pic.cheshi.com/prdcategory_2300_t6.html";
        string[183]="http://pic.cheshi.com/prdcategory_2300_t2.html";
        string[184]="http://pic.cheshi.com/prdcategory_1095_t6.html";
        string[185]="http://pic.cheshi.com/prdcategory_1095_t2.html";
        string[186]="http://pic.cheshi.com/prdcategory_2305_t6.html";
        string[187]="http://pic.cheshi.com/prdcategory_2305_t2.html";
        string[188]="http://pic.cheshi.com/prdcategory_2074_t6.html";
        string[189]="http://pic.cheshi.com/prdcategory_2074_t2.html";
        string[190]="http://pic.cheshi.com/prdcategory_1661_t6.html";
        string[191]="http://pic.cheshi.com/prdcategory_1661_t2.html";
        string[192]="http://pic.cheshi.com/prdcategory_1152_t6.html";
        string[193]="http://pic.cheshi.com/prdcategory_1152_t2.html";
        string[194]="http://pic.cheshi.com/prdcategory_1810_t2.html";
        string[195]="http://pic.cheshi.com/prdcategory_1810_t8.html";
        string[196]="http://pic.cheshi.com/prdcategory_2008_t6.html";
        string[197]="http://pic.cheshi.com/prdcategory_2008_t2.html";
        string[198]="http://pic.cheshi.com/prdcategory_2062_t6.html";
        string[199]="http://pic.cheshi.com/prdcategory_2062_t2.html";
        for(int i=0;i<string.length;i++){
        	System.out.println(i+1);
        	filter(string[i],i+1);
        	//char c='\'';
        	//System.out.println(c);
        }
	}

}
