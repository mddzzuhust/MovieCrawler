package crawler.timer;
import java.util.TimerTask;
import crawler.client.Client;

/**
 * 定时计划任务
 * @author xiaorui
*/
public class MyTask extends TimerTask {
	public void run(){
		try{
	     Client.main(null);
		}catch(Exception e)
		{
			
		}
    }
}
