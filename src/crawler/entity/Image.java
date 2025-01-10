package crawler.entity;

import java.util.HashMap;
import java.util.Map;

public class Image {

	// HTTP协议图像的contentType
	public static Map<String, String> MimeTypeImage = new HashMap<String, String>();
	
	static {
		/*MimeTypeImage.put("image/jpeg", "jpg,jpeg,jpe,jfif");
		MimeTypeImage.put("image/bmp", "bmp,bm");
		MimeTypeImage.put("image/x-windows-bmp", "bmp");
		MimeTypeImage.put("image/mjpeg", "jpg,jpeg,jpe,jfif");*/
		
		//格式只保存一个,我决定不求全,只求正确.
		MimeTypeImage.put("image/jpeg", "jpg,jpeg");
		//MimeTypeImage.put("image/gif", "gif");
		//MimeTypeImage.put("image/bmp", "bmp");
		//MimeTypeImage.put("image/x-windows-bmp", "bmp");
		//MimeTypeImage.put("application/x-bmp", "bmp");
		MimeTypeImage.put("image/png", "png");
		//MimeTypeImage.put("image/mjpeg", "jpg");
	}
	
	private String imageName;      // 文件名
	private String imageURL;        // 图片链接地址
	private String imageType;      // 文件类型
	private String physicsPath;    // 物理文件所在路径
	

	public String toString() {
		StringBuffer image = new StringBuffer();
		String lineSep = System.getProperty("line.separator");
		image.append("imageName=" + this.getImageName() + lineSep);
		image.append("imageURL=" + this.getImageURL() + lineSep);
		image.append("imageType=" + this.getImageType() + lineSep);
		image.append("physicsPath=" + this.getPhysicsPath() + lineSep);

		return image.toString();
	}

	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	public String getPhysicsPath() {
		return physicsPath;
	}
	public void setPhysicsPath(String physicsPath) {
		this.physicsPath = physicsPath;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String url) {
		this.imageURL = url;
	}
}