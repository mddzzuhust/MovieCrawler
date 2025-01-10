package crawler.filter;

public interface IFilter {
	
	// dropped this URL or not
	public boolean isFilterUrl(String url, String hostdomain);
	
}
