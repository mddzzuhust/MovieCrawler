package crawler.update;

import java.util.TimerTask;

/**
1. 从数据库获取Movie信息
2. 访问对应的链接,解析提取电影评分等可能变化的信息
3. 另一种情况，修改数据库信息(即使价格没有变化也得更新.更新爬行时间)
*/

public class Update extends TimerTask{
	
	private int startIndex;
	private int endIndex;
	public static int increase; // 增长量
	public static int threadNum = 1; // 线程数
	
	public Update(){
		startIndex =2;
		endIndex = 5;
		increase = threadNum * (endIndex - startIndex + 1);
	}
	
	public void execute(){
		for(int i = 0; i < threadNum; i++){
			
			Thread thread = new Thread(new UpdateThread(startIndex, endIndex), "thread#" + i);
			thread.start();
			endIndex = endIndex + endIndex - startIndex + 1;
			startIndex = ( startIndex + endIndex + 1)/2;
			//System.out.println(startIndex + "," + endIndex);
		}
	}
	
	public void run() {
		//Update update = new Update();
		execute();
	}
	public static void main(String []args) {
		Update update = new Update();
		update.execute();
	}
}
