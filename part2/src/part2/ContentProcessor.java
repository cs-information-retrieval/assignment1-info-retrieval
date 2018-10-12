package part2;

/**
 * Interface definition for removing noise from web page, as much as possible.
 * @author jlepere2
 * @date 09/28/2018
 */
public interface ContentProcessor {

	/**
	 * Extracts only the main content from a web page, as much as possible.
	 * @param html the html to process.
	 * @return the processed text.
	 */
	public String processContent(String html);
	
}
