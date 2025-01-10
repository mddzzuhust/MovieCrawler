package crawler.update;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import crawler.entity.Movie;
import crawler.parse.DefaultParse;
import crawler.mysql.SQLMovie;

public class UpdateThread implements Runnable{
	SQLMovie sqlMovie;
	int startIndex;
	int endIndex;
	long totalNum;
	long interval =  1*24*60*60*1000; // 时间间隔. 一天
	
	public UpdateThread(){
		startIndex = 0;
		endIndex = 99;
		sqlMovie = new SQLMovie();
	}
	
	public UpdateThread(int start, int end){
		startIndex = start;
		endIndex = end;
		sqlMovie = new SQLMovie();
		try {
			totalNum = sqlMovie.getTotalNum();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			List<Movie> movies = null;
			do{
				//bags.clear();
				//System.out.println(Thread.currentThread().getName() + ":[" + startIndex + "," + endIndex + "]. ");
				movies = sqlMovie.getMovies(startIndex, endIndex);
				System.out.println(Thread.currentThread().getName() + ":[" + startIndex + "," + endIndex + "]. " + movies.size());
				endIndex = endIndex + Update.increase;
				startIndex = startIndex + Update.increase;
				
				for(Movie movie : movies){
					Thread.sleep(2000);
					Timestamp time = new Timestamp(System.currentTimeMillis());
					if(time.getTime() - movie.getModifyTime().getTime() > interval){
						Movie newMovie = DefaultParse.Parse(movie);
						if(newMovie != null){
							newMovie.setId(movie.getId());
							if(sqlMovie.updateMovie(newMovie) == 1){
								System.out.println("更新成功.");
							} else {
								System.out.println("更新失败.");
							}
						} 
					}
				}
			} while(endIndex <= totalNum); // 这里需要改动,保证循环执行
			System.out.println("endIndex"+endIndex);
			System.out.println("退出." + Thread.currentThread().getName() + ":[" + (startIndex - Update.increase) + "," + (endIndex - Update.increase) + "]. " + movies.size());
		} catch (Exception e) {
			System.out.println("异常." + Thread.currentThread().getName());
			e.printStackTrace();
		}
   }
}
