package crawler;

import java.util.BitSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * URL判重类
 * @author liuxue
 */
public class BloomFilter {
	private static int defaultSize = 1 << 28;
	private static int basic = defaultSize - 1;
	private BitSet bits;
	
	public BloomFilter() {
		this(false);
	}
	
	/**
	 * 控制是否实行增量爬行.我以后可能会将它用在服务端,但现在没用
	 * @param increment true表示采用,false表示不采用
	 */
	public BloomFilter(boolean increment) {
		bits = new BitSet(defaultSize);
		if(increment) {
			File file = new File("BloomFilter.bits");
			if(file.exists()) {
				try	{
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
					BitSet tempt = (BitSet) ois.readObject();
					if(tempt != null) {
						addBits(tempt);
						tempt = null;
					}
					
					ois.close();
				} catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public void save() {
		if(bits != null) {
			try	{
				File file = new File("BloomFilter.bits");
				if(file.exists())
					file.delete();
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, true));
				oos.writeObject(this.bits);
				
				oos.close();
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * 判重是否包含该URL信息,即URL是否已被处理过
	 * @param url
	 * @return
	 */
	public boolean contains(String url) {
		if(url==null) {
			return true;
		}
		
		int pos1 = hash1(url);
		int pos2 = hash2(url);
		int pos3 = hash3(url);
		if(bits.get(pos1) && bits.get(pos2) && bits.get(pos3)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 判断是否包含该URL信息.若无,添加URL信息,返回由','连接多个哈希码组成的字符串
	 * @param url 待判断的URL地址
	 * @return 未包含:哈希字符串
	 *         包含:null
	 */
	public String checkAndAdd(String url) {
		if(url == null) {
			return null;
		}
		
		int pos1 = hash1(url);
		int pos2 = hash2(url);
		int pos3 = hash3(url);
		
		if(bits.get(pos1) && bits.get(pos2) && bits.get(pos3)) {
			return null;
		}
		
		bits.set(pos1);
		bits.set(pos2);
		bits.set(pos3);
		// 鉴于一个整数转化为字符串最大长度为10,三个整数加上两个逗号,最大长度为32
		return new StringBuilder(32).append(pos1).append(',').append(pos2)
				.append(',').append(pos3).toString();
	}
	
	public void add(String url) {
		if(url==null) {
			return;
		}
		
		int pos1 = hash1(url);
		int pos2 = hash2(url);
		int pos3 = hash3(url);
		
		bits.set(pos1);
		bits.set(pos2);
		bits.set(pos3);
	}
	
	public static String getHashcode(String url) {
		int pos1 = hash1(url);
		int pos2 = hash2(url);
		int pos3 = hash3(url);
		// 鉴于一个整数转化为字符串最大长度为10,三个整数加上两个逗号,最大长度为32
		return new StringBuilder(32).append(pos1).append(',').append(pos2)
				.append(',').append(pos3).toString();
	}
	
	public static int hash3(String line) {
		int h = 0;
		int len = line.length();
		for (int i = 0; i < len; i++) {
			h = 37 * h + line.charAt(i);
		}
		return check(h);
	}
	
	public static int hash2(String line) {
		int h = 0;
		int len = line.length();
		for (int i = 0; i < len; i++) {
			h = 33 * h + line.charAt(i);
		}
		return check(h);
	}

	public static int hash1(String line) {
		int h = 0;
		int len = line.length();
		for (int i = 0; i < len; i++) {
			h = 31 * h + line.charAt(i);
		}
		return check(h);
	}
	
	private static int check(int h) {
		return basic & h;
	}
	
	public BitSet getBits() {
		return this.bits;
	}
	
	public void setBits(BitSet bits) {
		this.bits = bits;
	}
	
	public boolean isPositionSet(int pos) {
		return this.bits.get(pos);
	}
	
	public void setPosition(int pos) {
		this.bits.set(pos);
	}
	
	/**
	 * 将bits与类中原有的BitSet结构进行or运算
	 * @param bits
	 */
	public void addBits(BitSet bits) {
		this.bits.or(bits);
	}
	
}
