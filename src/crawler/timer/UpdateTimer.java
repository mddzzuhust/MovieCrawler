package crawler.timer;

import java.util.Timer;
import crawler.update.Update;

public class UpdateTimer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub      
        Timer timer = new Timer();
        timer.schedule(new Update(), 1000, 86400000);
	}

}
