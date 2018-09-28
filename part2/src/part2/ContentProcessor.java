package part2;

import java.io.File;
import java.io.IOException;

/**
 * Interface definition for removing noise from web page, as much as possible.
 * @author jlepere2
 * @date 09/28/2018
 */
public interface ContentProcessor {

	/**
	 * Extracts only the main content from a web page, as much as possible.
	 * @param f the input file to process.
	 * @return the processed text.
	 * @throws IOException 
	 */
	public String processContent(File f) throws IOException;
	
}
