package crawler.parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

public class ParseInfo {
    public static void filter(String url){
    	List<String> ls=new ArrayList<String>();
        try{
        	Parser parser=new Parser();
			parser.setEncoding(parser.getEncoding());
		    URL urls = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection)urls.openConnection();     
		    connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		    parser.setConnection(connection);
		    
		    NodeFilter classNF=new HasAttributeFilter("id","sortTable3");
		    NodeList list=parser.extractAllNodesThatMatch(classNF);
		    //System.out.println(list.size());
            Tag tag=(Tag)list.elementAt(0);
            String str=tag.toHtml();
            //System.out.println(tag.toPlainTextString());
            //str=str.replaceAll("\n"," ");
            //str=str.replaceAll("\t","");
            //str=str.replaceAll(" ","");
            Map<Integer,String> map=new LinkedHashMap<Integer,String>();
            int i=0;
            int Number=0;
            while(i<str.length()){
            	i=str.indexOf("<u>",i);
            	if(i<0) break;
            	int j=str.indexOf("</u>",i);
            	String tmp=str.substring(i+3,j);
            	Number++;
            	map.put(Number,tmp);
            	i=i+3;
            }
            File file=new File("D:/info.txt");
            file.createNewFile();
            BufferedWriter out=new BufferedWriter(new FileWriter(file,true));
            Set s=map.keySet();
            Iterator iterator=s.iterator();
            while(iterator.hasNext()){
            	Object o=iterator.next();
            	String tmp=o+" "+map.get(o);
            	out.write(tmp);
            	out.newLine();
            }
            out.close();
            //System.out.println(str);
        }catch(Exception e){
        	e.printStackTrace();
        }
    } 
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        String url="http://db.auto.sohu.com/cxdata/salesindex.html";
        filter(url);
	}

}
