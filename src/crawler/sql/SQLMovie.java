package crawler.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import crawler.crawler.Crawler;
import crawler.entity.Image;
import crawler.entity.Movie;

public class SQLMovie implements ISQL {

	private Statement st;
	private Statement st1;
	private Statement st2;
	private Statement st3;	
    private ResultSet rs;  
    private ResultSet ri;        //用于存储图像查询结果
    private ResultSet rv;       //用于存储预告片查询结果
    private Pool pool;
    private Connection conn;
    private Movie movie;

    public SQLMovie() {
        st = null;
        rs = null;
        movie = null;
        pool = Pool.getInstance();
    }
    
    /**
     * 获取数据库记录总数.
     * 这里通过直接查询Oracle的序列
     */
    public int getTotalNum() throws SQLException {
        int total = 0;
        try {
        	conn = pool.getConnection();
        	if(conn == null)   	conn = pool.getConnection(3000);
            st = conn.createStatement();
            String sql = "select seq_movieid.nextval as totalrow from dual";
            rs = st.executeQuery(sql);
            if(rs.next())
                total = rs.getInt("totalrow");
        } finally {
        	if(rs != null)
        		rs.close();
        	if(st != null)
        		st.close();
            pool.freeConnection(conn);
        }
        return total;
    }

    
    /**
     * 增.插入记录
     * @param bag
     * @return
     * @throws SQLException
     * @author xiaorui
     */
    public int insertMovie(Movie movie) throws SQLException {
        int total = 0;
        int temp;
        PreparedStatement ps = null;
        HashSet<Image> imgSet=movie.getImgSet();
        HashSet<String> videoUrlSet=movie.getVideoUrlSet();
        Image image;
        try {
        	conn = pool.getConnection();
        	if(conn == null)   	conn = pool.getConnection(3000);
            ps = conn.prepareStatement(
            		"insert into idcmedia.movie(id,url,domain,crawlerTime,modifyTime,score,detail,title,abstract1,abstract2) "
            		+ "values(idcmedia.seq_movieid.nextval,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, movie.getUrl());
            ps.setString(2, movie.getDomain());
            ps.setTimestamp(3, movie.getCrawlerTime());
            ps.setTimestamp(4, movie.getModifyTime());
        

            ps.setString(5, movie.getScore());
            ps.setString(6, movie.getMovieInfo());
            ps.setString(7, movie.getTitle());
            ps.setString(8, movie.getMovieAbstract1());
            ps.setString(9, movie.getMovieAbstract2());
            total = ps.executeUpdate();
            ps.close();
            int flag=0;
            for(Iterator it=imgSet.iterator();it.hasNext();)
            {
            	image=(Image) it.next();
            	ps=conn.prepareStatement(
                         "insert into idcmedia.movieimage(id, imgName,imgUrl,imgType,physicsPath,cover,ip,relative_path,movie_id) "
	            		+"values(idcmedia.seq_movie_image_id.nextval,?,?,?,?,?,?,?,idcmedia.seq_movieid.currval)");
	   	
	            ps.setString(1, image.getImageName());
	            ps.setString(2, image.getImageURL());
	            ps.setString(3, image.getImageType());
	            ps.setString(4, image.getPhysicsPath());
	            if(flag == 0){
	            	ps.setString(5, "1");
	            	flag = 1;
	            }else{
	            	ps.setString(5, "0");
	            }
	            ps.setString(6, Crawler.rootAddress);
	            temp = image.getPhysicsPath().indexOf("image") + 6;
	            ps.setString(7, image.getPhysicsPath().substring(temp));
	            ps.executeUpdate();
                ps.close();
            }
            
            for(Iterator it=videoUrlSet.iterator();it.hasNext();)
            {
            	ps=conn.prepareStatement("insert into idcmedia.movievideo(id,videourl,movie_id) "
            			+"values(idcmedia.seq_movie_video_id.nextval,?,idcmedia.seq_movieid.currval)");
            	ps.setString(1, it.next().toString());
            	ps.executeUpdate();
            	ps.close();
            }
            
        } catch(SQLException e){
        	e.printStackTrace();
        	System.out.println(movie.toString());
        } finally {
        	if(ps != null)
        		ps.close();
        	pool.freeConnection(conn);
        }
        return total;
    }
    
    /**
     * 删除电影信息
     * @param imageid
     * @return 删除成功返回1,无对应记录返回0
     * @throws SQLException
     * @author xiaorui
     */
    public int deleteMovie(int imageid) throws SQLException {
        int total = 0;
        try {
        	conn = pool.getConnection();
        	if(conn == null)   	conn = pool.getConnection(3000);
            st = conn.createStatement();
            total = st.executeUpdate("DELETE FROM IDCMEDIA.movie WHERE id=" + imageid);
            st.executeUpdate("DELETE FROM IDCMEDIA.movieimage WHERE movie_id="+imageid);
            st.executeUpdate("DELETE FROM IDCMEDIA.MOVIEVIDEO WHERE movie_id="+imageid);
        } finally {
        	if(st != null)
        		st.close();
        	pool.freeConnection(conn);
        }
        return total;
    }

    /**
     * 改.主要是评分信息变动
     * @param image
     * @return
     * @throws SQLException
     */
    public int updateMovie(Movie movie) throws SQLException {
    	int total = 0;
    	PreparedStatement ps = null;
        HashSet<Image> imgSet=movie.getImgSet();
        HashSet<String> videoUrlSet=movie.getVideoUrlSet();
        Image image;
    	try {
    		conn = pool.getConnection();
         	if(conn == null)   	conn = pool.getConnection(3000);     
			for(Iterator it=imgSet.iterator();it.hasNext();)            //更新电影海报
			{
			     image=(Image) it.next();
			   	 ps=conn.prepareStatement(
			            "insert into idcmedia.movieimage(id, imgName,imgUrl,imgType,physicsPath,cover,ip,relative_path,movie_id) "
			  	        +"values(idcmedia.seq_movie_image_id.nextval,?,?,?,?,?,?,?,?)");
			     ps.setString(1, image.getImageName());
			  	 ps.setString(2, image.getImageURL());
			  	 ps.setString(3, image.getImageType());
			  	 ps.setString(4, image.getPhysicsPath());
			  	 ps.setString(5, "0");
		         ps.setString(6, Crawler.rootAddress);
		         int temp = image.getPhysicsPath().indexOf("image") + 6;
		         ps.setString(7, image.getPhysicsPath().substring(temp));
			  	 ps.setInt(8, movie.getId());
			  	 ps.executeUpdate();
			     ps.close();
		    }
			for(Iterator it=videoUrlSet.iterator();it.hasNext();)    //更新电影预告片
	        {
	            ps=conn.prepareStatement("insert into idcmedia.movievideo(id,videourl,movie_id) "
	            			+"values(idcmedia.seq_movie_video_id.nextval,?,?)");
	            ps.setString(1, it.next().toString());
	            ps.setInt(2, movie.getId());
	            ps.executeUpdate();
	            ps.close();
	        }
        	String sql = "UPDATE IDCMEDIA.MOVIE SET " +              //更新电影评分
        		    "score='" + movie.getScore() +
        			"',modifyTime=sysdate where id= " + movie.getId();
        //	System.out.println(sql);
        	
            st = conn.createStatement();
            total = st.executeUpdate(sql);
        } finally {
        	if(st != null)
        		st.close();
        	if(ps!=null)
        		ps.close();
        	pool.freeConnection(conn);
        }
        return total;
    }
    
    /**
     * 查.
     * @param id
     * @return 返回单条记录
     * @throws SQLException
     */
    public Movie getMovie(int id) throws SQLException {
        Movie movie = null;
        HashSet<Image> imgSet=new HashSet<Image>();
        HashSet<String> videoUrlSet=new HashSet<String>();
        Image image;
        
        try {
        	conn = pool.getConnection();
        	if(conn == null)   	conn = pool.getConnection(3000);
            st = conn.createStatement();
            rs = st.executeQuery("select * from idcmedia.MOVIE where id=" + id);
            ri = st.executeQuery("select * from idcmedia.MOVIEIMAGE where movie_id=" + id);
            rv = st.executeQuery("select * from idcmedia.MOVIEVIEDO where movie_id=" + id);
            if(rs.next()){
            	movie = new Movie();
            	movie.setId(rs.getInt("id"));
            	movie.setUrl(rs.getString("url"));
            	movie.setDomain(rs.getString("domain"));
            	movie.setCrawlerTime(rs.getTimestamp("crawlerTime"));
            	movie.setModifyTime(rs.getTimestamp("modifyTime"));
            	movie.setScore(rs.getString("score"));
            	movie.setMovieInfo(rs.getString("detail"));
            	movie.setMovieAbstract1(rs.getString("abstract1"));
            	movie.setMovieAbstract2(rs.getString("abstract2"));
            	
            	while(ri.next())
            	{
            	    image=new Image();
	                image.setImageName(ri.getString("imgName"));
	                image.setImageURL(ri.getString("imgUrl"));
	                image.setImageType(ri.getString("imgType"));
	            	image.setPhysicsPath(ri.getString("physicsPath"));
	                imgSet.add(image);
            	}
            	movie.setImgSet(imgSet);
            	
            	while(rv.next())
            	{
            		videoUrlSet.add(rv.getString("videoUrl"));
            	}
            	movie.setVideoUrlSet(videoUrlSet);
            }
        } finally {
        	if(rv !=null)
        		rv.close();
        	if(ri !=null)
        		ri.close();
        	if(rs != null)
        		rs.close();
        	if(st != null)
        		st.close();
        	
            pool.freeConnection(conn);
        }
        return movie;
    }
    

    /**
     * 查.根据id范围获取多条记录
     * @param beginID 
     * @param endID 
     * @return 返回多条记录
     * @throws SQLException
     */
    public List<Movie> getMovies(int beginID, int endID) throws SQLException {
    	List<Movie> list = new ArrayList<Movie>();
    	HashSet<Image> imgSet = new HashSet<Image>();
    	HashSet<String> videoUrlSet = new HashSet<String>();
    	Image image;
        try {
        	conn = pool.getConnection();
        	if(conn == null)   	conn = pool.getConnection(3000);
            st1 = conn.createStatement();
            rs = st1.executeQuery("select * from idcmedia.Movie where id >=" + beginID
            		+ " and id <= " + endID);
            //System.out.println("[" + beginID + "," + endID + "]" + rs.hashCode() + "   " + rs.toString());
            for(; rs.next(); list.add(movie)) {
            	movie = new Movie();
            	movie.setId(rs.getInt("id"));
            	movie.setUrl(rs.getString("url"));
            	movie.setDomain(rs.getString("domain"));
            	movie.setCrawlerTime(rs.getTimestamp("crawlerTime"));
            	movie.setModifyTime(rs.getTimestamp("modifyTime"));
            
            	movie.setScore(rs.getString("score"));
            	movie.setMovieInfo(rs.getString("detail"));
            	movie.setMovieAbstract1(rs.getString("abstract1"));
            	movie.setMovieAbstract2(rs.getString("abstract2"));
            	
            	st2 = conn.createStatement();
            	ri = st2.executeQuery("select * from idcmedia.MOVIEIMAGE where movie_id=" + movie.getId());
            	while(ri.next())
            	{
            	    image=new Image();
	                image.setImageName(ri.getString("imgName"));
	                image.setImageURL(ri.getString("imgUrl"));
	                image.setImageType(ri.getString("imgType"));
	            	image.setPhysicsPath(ri.getString("physicsPath"));
	                imgSet.add(image);
	                movie.getImgUrlSet().add(image.getImageURL());
            	}
            	movie.setImgSet(imgSet);
            	ri.close();
            	st2.close();
            	
            	st3 = conn.createStatement();
            	rv = st3.executeQuery("select * from idcmedia.MOVIEVIDEO where movie_id="+movie.getId());
            	while(rv.next())
            	{
            		videoUrlSet.add(rv.getString("videoUrl"));
            	}
            	movie.setVideoUrlSet(videoUrlSet);
            	
            	rv.close();
            	st3.close();
            }
        } finally {
        	if(rv !=null){
        		rv.close();
        	}
        	
        	if(ri !=null){
        		ri.close();
        	}
        	if(rs != null){
        		//System.out.println("关闭[" + beginID + "," + endID + "]" + rs.hashCode() + "   " + rs.toString());
        		rs.close();
        	}
        	if(st1 != null)
        		st1.close();
        	if(st2 != null)
        		st2.close();
        	if(st3 !=null)
        		st3.close();
            pool.freeConnection(conn);
        }
        return list;
    }
    
    public List<Movie> getMovies (String sqlCondition) throws SQLException {
        List<Movie> list = new ArrayList<Movie>();
        HashSet<Image> imgSet=new HashSet<Image>();
        HashSet<String> videoUrlSet= new HashSet<String>();
    	Image image;
        try {
        	conn = pool.getConnection();
        	if(conn == null)   	conn = pool.getConnection(3000);
            st1 = conn.createStatement();
            rs = st1.executeQuery(sqlCondition);

            for(; rs.next(); list.add(movie)) {
            	movie = new Movie();
            	movie.setId(rs.getInt("id"));
            	movie.setUrl(rs.getString("url"));
            	movie.setDomain(rs.getString("domain"));
            	movie.setCrawlerTime(rs.getTimestamp("crawlerTime"));
            	movie.setModifyTime(rs.getTimestamp("modifyTime"));
            
            	movie.setScore(rs.getString("score"));
            	movie.setMovieInfo(rs.getString("detail"));
            	movie.setMovieAbstract1(rs.getString("abstract1"));
            	movie.setMovieAbstract2(rs.getString("abstract2"));
            	
            	st2 = conn.createStatement();
            	ri = st2.executeQuery("select * from idcmedia.MOVIEIMAGE where movie_id=" + movie.getId());
            	while(ri.next())
            	{
            	    image=new Image();
	                image.setImageName(ri.getString("imgName"));
	                image.setImageURL(ri.getString("imgUrl"));
	                image.setImageType(ri.getString("imgType"));
	            	image.setPhysicsPath(ri.getString("physicsPath"));
	                imgSet.add(image);
            	}
            	movie.setImgSet(imgSet);
            	
            	ri.close();
            	st2.close();
            	
            	st3 = conn.createStatement();
	            rv = st3.executeQuery("select * from idcmedia.MOVIEVIDEO where movie_id="+movie.getId());         	
            	while(rv.next())
            	{
            		videoUrlSet.add(rv.getString("videoUrl"));
            	}
            	movie.setVideoUrlSet(videoUrlSet);
            	
            	rv.close();
            	st3.close();
            }
        } finally {
        	if(rv !=null)
        		rv.close();
        	if(ri !=null)
        		ri.close();
        	if(rs != null)
        		rs.close();
        	if(st1 != null)
        		st1.close();
        	if(st2 != null)
        		st2.close();
        	if(st3 != null)
        		st3.close();
            pool.freeConnection(conn);
        }
        return list;
    }

    public boolean executeSQL(String sqlCondition) throws SQLException {
    	boolean done = false;
        try {
        	conn = pool.getConnection();
        	if(conn == null)   	conn = pool.getConnection(3000);
            st = conn.createStatement();
            done = st.execute(sqlCondition);
        } finally {
        	if(st != null)
        		st.close();
        	pool.freeConnection(conn);
        }
        return done;
    }
    
    public int getSeqImgName()  throws SQLException {
    	int index = 0;
        try {
        	conn = pool.getConnection();
        	if(conn == null)   	conn = pool.getConnection(3000);
				st = conn.createStatement();
            String sql = "select seq_movie_imgname.nextval as imgname from dual";
            rs = st.executeQuery(sql);
            if(rs.next())
            	index = rs.getInt("imgname");
        } catch (SQLException e) {
        	e.printStackTrace();
        } finally {
        	if(rs != null)
        		rs.close();
        	if(st != null)
        		st.close();
            pool.freeConnection(conn);
        }
        return index;
    }
    
    public boolean urlFilter(String url) throws SQLException{
    	String sql = null;
    	boolean flag = false;
    	
    	if(url.indexOf("'")!=-1)
    		return false;
    	
    	
    	try {
         conn = pool.getConnection();
         if(conn == null)   	conn = pool.getConnection(3000);
         st = conn.createStatement();
         sql = "select url from idcmedia.movie where url = '" + url + "'";
         rs = st.executeQuery(sql);
         if(rs.next())
        	 flag = true;
         else
        	 flag = false;
    	}catch(Exception e)
    	{
    		System.out.println(sql);
    		e.printStackTrace();
    		
    	}finally{
    		if(rs != null)
        		rs.close();
        	if(st != null)
        		st.close();
            pool.freeConnection(conn);
    		
    	}
    	return flag;	
    }
    
    public static void main(String[] args){
    	SQLMovie test = new SQLMovie();
    	boolean f1;
		try {
			f1 = test.urlFilter("http://movie.douban.com/subject/24529338/{{= ep['play_link']}}");
			if(f1)
	        	System.out.println("1");
	        else 
	        	System.out.println("0");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    
    }
}
