package crawler.parse;

import java.util.HashSet;

import org.apache.commons.math.util.MultidimensionalCounter.Iterator;
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

import crawler.fetcher.DefaultFetcher;
import crawler.entity.Image;
import crawler.entity.Movie;
import crawler.entity.Page;
import crawler.url.URLUtil;

public class ParseM1905 {
	
	/**
	* 功能：解析m1905电影网电影信息,提取电影的导演、编剧、主演、类型以及电影的海报链接和所要分析网页的所有链接等
	* 原理：采用Filter访问html,Filter是根据某种条件过滤取出需要的Node再进行处理
	* @author XiaoRui 
	*/
	public static HashSet<String> filter(String url, Movie movie){
		HashSet<String> linkSet = new HashSet<String>();    
		HashSet<String> imgUrlSet=new HashSet<String>();
		HashSet<String> videoUrlSet=new HashSet<String>();
		Parser parser = new Parser();
		try{
			try{
			parser.setURL(url);
			parser.setEncoding(parser.getEncoding());
			}catch(Exception e)
			{
				parser.reset();
				System.out.println(url);
			}
			
			// 链接/Meta标签
			NodeFilter linkNF = new NodeClassFilter(LinkTag.class);
			NodeFilter metaNF = new NodeClassFilter(MetaTag.class);
		
			//电影的详细信息
		
			NodeFilter movieInfoNF = new HasAttributeFilter("class","movStaff line_BSld");

			NodeFilter movieAbstractNF = new HasAttributeFilter("class","pt10 g6e_f line-h22 f12 movBOXp");
			//电影的评分
			NodeFilter spanNF=new NodeClassFilter(Span.class);
		    NodeFilter spanAttNF=new HasAttributeFilter("class","score");
		    NodeFilter spanAF=new AndFilter(new NodeFilter[]{spanNF, spanAttNF});
			
			//电影的海报图片地址
			NodeFilter imgNF=new HasAttributeFilter("class","pt06 g6e_f line-h22 f12 laINpicB tabcontent");

			//电影预告片链接地址
			NodeFilter videoNF=new HasAttributeFilter("class","conTABLE cl pb40 tabscontainer02");
			
			OrFilter filter=new OrFilter(new NodeFilter[]{linkNF, metaNF, movieInfoNF, movieAbstractNF, spanAF,imgNF,videoNF});
			NodeList list=parser.extractAllNodesThatMatch(filter);

			boolean flag = false;
			boolean mark = true;
			for(int i=0;i<list.size();i++){
				Tag tag = (Tag)list.elementAt(i);
				if (tag instanceof LinkTag) {
					LinkTag linkTag = (LinkTag)tag;
		        	if(linkTag.isHTTPLikeLink()){
		        		String str = linkTag.extractLink(); 
		        		linkSet.add(str);
		        	}
				}else if(tag instanceof MetaTag){
					String temp = tag.getAttribute("name");
					if (temp != null && "description".equalsIgnoreCase(temp)) {
						String content = tag.getAttribute("content");
						
						if(content != null && content.contains("完整电影信息")){	
							String title=content.substring(0,content.indexOf("电影完整版"));
							movie.setTitle(title);
							movie.setUrl(url);
							movie.setDomain(URLUtil.getDomain(url));
							flag = true;
						}
					}
				}else if(flag && "pt10 g6e_f line-h22 f12 movBOXp".equalsIgnoreCase(tag.getAttribute("class"))){
					String str = tag.toPlainTextString().replaceAll("\n|\r", "").trim();				
					if(mark){
						int temp = str.indexOf("展开");
						if(temp != -1){
							str = str.substring(0,temp);
						}
						movie.setMovieAbstract1(str);	
						mark = false;
					}else{
						int temp = str.indexOf("收起");
						if(temp != -1){
							str = str.substring(0,temp);
						}
						movie.setMovieAbstract2(str);
					}					
				}else if(flag && "movStaff line_BSld".equalsIgnoreCase(tag.getAttribute("class"))) {      //获取电影的详细信息

				//	System.out.println(tag.toHtml());
				    String str=tag.toPlainTextString();
				   // System.out.println(str);
				    str=str.replaceAll(" ", "");
				    str=str.replaceAll("\\t", "");
				    int temp=str.lastIndexOf("添加基因");
				    if(temp!=-1)
				    str=str.substring(0, temp).trim();
				    movie.setMovieInfo(str);
				  //  System.out.println("电影信息"+'\n'+str);
			    }else if(flag && tag instanceof Span)             //获取电影的评分
		        {
				    String str=tag.toPlainTextString();
				    str=str+"分";
				    movie.setScore(str);
				//	System.out.println("电影评分"+'\n'+str);
			    }else if(flag && tag instanceof Div){                    //获取电影的海报图片链接地址
	
			    	if("pt06 g6e_f line-h22 f12 laINpicB tabcontent".equals(tag.getAttribute("class"))){
				    	String str=tag.toHtml();
				    	int temp=str.indexOf("href");
				    	if(temp!=-1)
				    	{	temp=temp+6;
					    	str=str.substring(temp, str.indexOf("\"",temp));
					    	//System.out.println(str);
					    	if(!str.startsWith("http://www.1905.com/film/photo/")){	//过滤无关链接
					    	imgUrlSet=imgUrlFilter(str);
					    	if(imgUrlSet.size()!=0)
					    	movie.setImgUrlSet(imgUrlSet);
					    	}
				    	}
			    	}else if("conTABLE cl pb40 tabscontainer02".equals(tag.getAttribute("class"))){    //获取电影的预告片链接
			    		String str=tag.toHtml();	
		    			int temp=str.indexOf("href")+6;
		    			String std=str.substring(temp, str.indexOf("</a>", temp));
		    			if(std.contains("预告片"))
		    			{
			    			str=str.substring(temp,str.indexOf("\"",temp));
			    			videoUrlSet=videoUrlFilter(str);
			    			if(videoUrlSet.size()!=0)
			    			movie.setVideoUrlSet(videoUrlSet);
		    			  //  System.out.println(str);
		    			}
			    		
			    	}
			    }	
		    }
			
		}catch(Exception e)
		{
			System.out.println(url);
			e.printStackTrace();
		}
		return linkSet;

    }
	/**
	 * 功能：获取电影海报图片链接集合
	 * @author XiaoRui
	 */
    public static HashSet<String> imgUrlFilter(String url)
	{
		HashSet<String> set = new HashSet<String>();
		boolean flag = false;
		String title=null;
		try{
			Parser parser=new Parser();
			parser.setURL(url);
			parser.setEncoding(parser.getEncoding());
			
			NodeFilter titleNF = new NodeClassFilter(TitleTag.class);
			
			NodeFilter divNF =new NodeClassFilter(Div.class);
			NodeFilter divAttF = new HasAttributeFilter("class","inner");
			
			
			NodeFilter divAF =new AndFilter(new NodeFilter[]{divAttF,divNF});
			
			NodeFilter filter= new OrFilter(new NodeFilter[]{titleNF,divAF});
			
			NodeList list=parser.extractAllNodesThatMatch(filter);
		    for(int i=0;i<list.size();i++)
		    {
		    	Tag tag = (Tag)list.elementAt(i);
			    if (tag instanceof TitleTag) {
					 title = tag.getFirstChild().getText(); 
					if(title != null && title.contains("电影海报"))
					flag = true;
				//	System.out.println(title);
				}else if(flag && tag instanceof Div)
				{
			    	String str=tag.toHtml();
			    	int temp=str.indexOf("href");
			    	if(temp!=-1)
			    	{	
				    	str=str.substring(temp+6, str.indexOf("\"",temp+6));
				    //	System.out.println(URLUtil.getUrlType(str));
				    	if(str != null && URLUtil.getUrlType(str)== Page.IMAGE)
				    	{  set.add(str);
				    	  // System.out.println("图片链接："+str);
				    	}
			    	}
				}
		    }
		}catch(Exception e)
		{
			System.out.println(url);
			e.printStackTrace();
		}
		
		//System.out.println(set.size());
		return set;
	}
	/**
	 * 功能：获取电影预告片链接集合
	 * @author XiaoRui
	 */
    public static HashSet<String> videoUrlFilter(String url)
    {
    	HashSet<String> set = new HashSet<String>();
    	try{
    		Parser parser=new Parser();
    		parser.setURL(url);
    		parser.setEncoding(parser.getEncoding());
    		
    		NodeFilter divNF = new NodeClassFilter(Div.class);
    		NodeFilter divAttF = new HasAttributeFilter("class","flDivMov ");
    		NodeFilter divAF = new AndFilter(divNF,divAttF);
    		
    		NodeList list = parser.extractAllNodesThatMatch(divAF);
    		
    		for(int i=0;i<list.size();i++)
    		{
    			Tag tag=(Tag) list.elementAt(i);
    			
    			if(tag instanceof Div)
    			{
    				String str=tag.toHtml();
			    	int temp=str.indexOf("href");
			    	if(temp!=-1)
			    	{	
				    	str=str.substring(temp+6, str.indexOf("\"",temp+6));
				    	//System.out.println(title);
				    	if(str!=null)
				    	set.add(str);
				    	//System.out.println("视频链接："+str);
			    	}
    			}
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return set;
    }
  
    /**
	 * 回访已爬过的链接.用于爬虫更新机制
	 * @param url
	 * @return
     * @throws Exception 
	 */
	public static Movie filter(Movie movie) throws Exception {
		Movie newMovie = null;	
		HashSet<String> imgUrlSet=new HashSet<String>();
		HashSet<String> videoUrlSet=new HashSet<String>();
		DefaultFetcher fetcher = new DefaultFetcher();
		try{
			Parser parser=new Parser();
			parser.setURL(movie.getUrl());
			parser.setEncoding(parser.getEncoding());
			
			//电影的评分
			NodeFilter spanNF=new NodeClassFilter(Span.class);
		    NodeFilter spanAttNF=new HasAttributeFilter("class","score");
		    NodeFilter spanAF=new AndFilter(new NodeFilter[]{spanNF, spanAttNF});
		
		    //电影的海报
		    NodeFilter imgNF=new HasAttributeFilter("class","pt06 g6e_f line-h22 f12 laINpicB tabcontent");
		    
		  //电影预告片链接地址
			NodeFilter videoNF=new HasAttributeFilter("class","conTABLE cl pb40 tabscontainer02");
		    
		    OrFilter filter = new OrFilter(new NodeFilter[]{spanAF,imgNF,videoNF});
		    NodeList list=parser.extractAllNodesThatMatch(filter);
		    
		    newMovie = new Movie();
		    newMovie.setId(movie.getId());
		    for(int i=0;i<list.size();i++){
		    	Tag tag = (Tag)list.elementAt(i);
		    	
		    	if(tag instanceof Span)             //获取电影的评分
		        {
				    String str=tag.toPlainTextString();
				    str=str+"分";
				    newMovie.setScore(str);
					//System.out.println("电影评分"+'\n'+str);
			    }else if(movie.getImgSet().size() < 5 && tag instanceof Div){                    //获取电影的海报图片链接地址
			    	if("pt06 g6e_f line-h22 f12 laINpicB tabcontent".equals(tag.getAttribute("class"))){
				    	String str=tag.toHtml();
				    	int temp=str.indexOf("href");
				    	if(temp!=-1){	
				    		temp=temp+6;
					    	str=str.substring(temp, str.indexOf("\"",temp));
					    	//System.out.println(str);
					    	if(!str.startsWith("http://www.1905.com/film/photo/")){	//过滤无关链接
					    			imgUrlSet=imgUrlFilter(str);
    						    	if(imgUrlSet.size()!=0){
					    			imgUrlSet.removeAll(movie.getImgUrlSet());
					    			newMovie.setImgUrlSet(imgUrlSet);//移除重复的海报图片链接，获取新的海报图片链接
     					    	}
					    	}
				    	}
			    	}	
			    }else if("conTABLE cl pb40 tabscontainer02".equals(tag.getAttribute("class"))){    //获取电影的预告片链接
		    		String str=tag.toHtml();	
	    			int temp=str.indexOf("href")+6;
	    			String std=str.substring(temp, str.indexOf("</a>", temp));
	    			if(std.contains("预告片"))
	    			{
		    			str=str.substring(temp,str.indexOf("\"",temp));
		    			videoUrlSet=videoUrlFilter(str);
		    			if(videoUrlSet.size()!=0){
		    				videoUrlSet.removeAll(movie.getVideoUrlSet());
		    				newMovie.setVideoUrlSet(videoUrlSet);
		    			}
	    			}	
		    	}
		    }	
		}catch(Exception e){
			System.out.println(newMovie.getUrl());
			e.printStackTrace();
		}
	
		if(newMovie.getImgSet().size()>0){
            fetcher.setUpdateFlag(true);
			fetcher.downloadImage(newMovie);   //在运行更新线程时，需要设置默认配置文件，使更新的海报图片下载到正确的位置
		}
	   return newMovie;
	}
    
    
	public static void main(String []args)
	{
		String url="http://www.1905.com/mdb/film/2222142/";
		//String url="http://www.m1905.com/mdb/film/";
		//String url="http://www.m1905.com/mdb/film/list/mtype-6-year-2014-tag-1159/";
		Movie movie=new Movie();
		ParseM1905.filter(url,movie);
	    
		System.out.println(movie.toString());
	}
}
