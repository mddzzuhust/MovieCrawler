package crawler.entity;

import java.sql.Timestamp;
import java.util.HashSet;

import java.util.Iterator;

import crawler.entity.Image;

public class Movie {
	
	private int id;           // ID号
	private String url; // 电影页面链接
	private String domain; // 域名
	private Timestamp crawlerTime; // 爬行时间
	private Timestamp modifyTime;  // 网站修改时间
	

	private HashSet<Image> imgSet=new HashSet<Image>();        //电影海报图像集合
	private String score;    //评分
	private String title;   //电影名称
	private String movieInfo;  //电影详细信息
	private String movieAbstract1;  //电影简介1（简单）
	private String movieAbstract2;  //电影简介2（详细）

	private HashSet<String> imgUrlSet = new HashSet<String>();     //海报图片链接地址集合
	private HashSet<String> videoUrlSet = new HashSet<String>();    //电影预告片地址集合

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public HashSet<String> getVideoUrlSet() {
		return videoUrlSet;
	}
	public void setVideoUrlSet(HashSet<String> videoUrlSet) {
		this.videoUrlSet = videoUrlSet;
	}
	public HashSet<Image> getImgSet() {
		return imgSet;
	}
	public void setImgSet(HashSet<Image> imgSet) {
		this.imgSet = imgSet;
	}
	
	public HashSet<String> getImgUrlSet() {
		return imgUrlSet;
	}
	public void setImgUrlSet(HashSet<String> imgUrlSet) {
		this.imgUrlSet = imgUrlSet;
		Image image;
		for(Iterator it=imgUrlSet.iterator();it.hasNext();)
		{
			image=new Image();
			image.setImageURL(it.next().toString());
			this.imgSet.add(image);
			//System.out.println(it.next());
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Timestamp getCrawlerTime() {
		return crawlerTime;
	}
	public void setCrawlerTime(Timestamp crawlerTime) {
		this.crawlerTime = crawlerTime;
	}
	public Timestamp getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getMovieInfo() {
		return movieInfo;
	}
	public void setMovieInfo(String movieInfo) {
		this.movieInfo = movieInfo;
	}
	public String getMovieAbstract1() {
		return movieAbstract1;
	}
	public void setMovieAbstract1(String movieAbstract1) {
		this.movieAbstract1 = movieAbstract1;
	}
	public String getMovieAbstract2() {
		return movieAbstract2;
	}
	public void setMovieAbstract2(String movieAbstract2) {
		this.movieAbstract2 = movieAbstract2;
	}
	
	public String toString(){
		
		StringBuffer sb = new StringBuffer();
		String lineSep = System.getProperty("line.separator");
		sb.append("编号:" + this.getId() + lineSep);
		sb.append("页面链接:" + this.getUrl() + lineSep);
		sb.append("域名:" + this.getDomain() + lineSep);
		sb.append("爬行时间:" + this.getCrawlerTime() + lineSep);
		sb.append("最后修改时间:" + this.getModifyTime() + lineSep);
		sb.append("电影名称："+ this.getTitle()+lineSep);
		sb.append("电影详细信息:" + this.getMovieInfo() + lineSep);
		sb.append("电影简介1:" + this.getMovieAbstract1() + lineSep);
		sb.append("电影简介2:" + this.getMovieAbstract2() + lineSep);
		
		sb.append("电影海报图片:");
		for(Iterator it=imgSet.iterator();it.hasNext();)
		{
			sb.append(it.next().toString());
		}
		sb.append(lineSep);
		sb.append("评分:" + this.getScore() + lineSep);
		//sb.append("" + this.img.toString() + lineSep);
		return sb.toString();
	}

}
