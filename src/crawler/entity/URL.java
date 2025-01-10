//package crawler.entity;
//
//import java.net.HttpURLConnection;
//import java.sql.Timestamp;
//
///**
// * 暂时未用到
// */
//public class URL {
//
//	private int id;
//	private int type;              // url类型,text:0 image:1 audio:2 video:3
//	private int flag;              // 该地址是否仍有效
//	private int hashcode;          // url哈希值
//	private String url;            // url内容
//	private String urlHost;        // 主机名
//	private String checkInHash;    // url判重哈希字符串.由三个整数用","连成的字符串
//	private Timestamp crawlerTime; // 爬行时间
//	private Timestamp modifyTime;  // 网站修改时间
//	private String reserve;        // 保留字段
//	
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
//	public int getType() {
//		return type;
//	}
//	public void setType(int type) {
//		this.type = type;
//	}
//	public int getFlag() {
//		return flag;
//	}
//	public void setFlag(int flag) {
//		this.flag = flag;
//	}
//	public int getHashcode() {
//		return hashcode;
//	}
//	public void setHashcode(int hashcode) {
//		this.hashcode = hashcode;
//	}
//	public String getUrl() {
//		return url;
//	}
//	public void setUrl(String url) {
//		this.url = url;
//	}
//	public String getUrlHost() {
//		return urlHost;
//	}
//	public void setUrlHost(String urlHost) {
//		this.urlHost = urlHost;
//	}
//	public String getCheckInHash() {
//		return checkInHash;
//	}
//	public void setCheckInHash(String checkInHash) {
//		this.checkInHash = checkInHash;
//	}
//	public Timestamp getCrawlerTime() {
//		return crawlerTime;
//	}
//	public void setCrawlerTime(Timestamp crawlerTime) {
//		this.crawlerTime = crawlerTime;
//	}
//	public Timestamp getModifyTime() {
//		return modifyTime;
//	}
//	public void setModifyTime(Timestamp modifyTime) {
//		this.modifyTime = modifyTime;
//	}
//	public String getReserve() {
//		return reserve;
//	}
//	public void setReserve(String reserve) {
//		this.reserve = reserve;
//	}
//	public HttpURLConnection openConnection() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//}
