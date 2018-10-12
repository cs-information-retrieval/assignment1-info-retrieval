import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Remove tags and extract words from html or partial html after content extraction.
 * @author jlepere2
 * @date 10/12/2018
 */
public class WordExtraction {

	
	/**
	 * Extract only the words from all files in args[0] folder and puts results to args[1] folder.
	 * @param args the input and output folders.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// validate command line arguments
		if (args.length != 2) {
			throw new IllegalArgumentException("args[0] = inFolder, args[1] = outFolder");
		}
		
		// extract input and output folders
		File inFolder = new File(args[0]);
		File outFolder = new File(args[1]);
		
		// for each file in the input folder
		for (File f : inFolder.listFiles()) {
			if (!f.isFile()) continue;
			
			// feedback
			System.out.println("Extracting from: " + f.getName());
			
			// extract the words from the file
			List<String> words = extractWords(extractHTML(f));
			
			// write the words to file
			writeWords(words, new File(outFolder.getPath(), f.getName() + ".txt").getPath());
			
		}
		
	}
	
	/**
	 * Extracts the HTML, without spaces, from the file.
	 * @param f the file containing the HTML
	 * @return a string of the HTML, without spaces
	 * @throws FileNotFoundException file not found exception
	 */
	public static String extractHTML(File f) throws FileNotFoundException {
		Scanner fileScanner = new Scanner(f);
		String fullHTML = "";
		while (fileScanner.hasNextLine()) {
			fullHTML += fileScanner.nextLine();
		}
		fileScanner.close();
		return fullHTML;
	}
	
	/**
	 * Write the extracted words to file.
	 * @param words the extracted words
	 * @param filepath the filepath
	 * @throws IOException io exception
	 */
	public static void writeWords(List<String> words, String filepath) throws IOException {
		FileWriter writer = new FileWriter(new File(filepath));
		for (String word : words) {
			writer.write(word + "\n");
		}
		writer.close();
	}
	
	/**
	 * Extracts the words from the html text.
	 * @param html the html
	 * @return a list of words
	 * @throws FileNotFoundException
	 */
	public static List<String> extractWords(String html) throws FileNotFoundException {
		
		// result list of words
		List<String> words = new LinkedList<>();
		
		// string buffer for a tag of characters between tags
		String s = "";
		
		// scan the full HTML text, one character at a time
		Scanner fullHTMLScanner = new Scanner(html);
		fullHTMLScanner.useDelimiter("");
		while (fullHTMLScanner.hasNext()) {
			
			// get the next character
			String c = fullHTMLScanner.next();
			
			if (c.equals("<")) {
				// start of a new tag
				//  add each word that was found in between the previous tag and this tag
				for (String word : s.trim().split(" ")) {
					if (!word.isEmpty()) {
						words.add(word.toLowerCase());
					}
				}
			} else if (c.equals(">")) {
				// end of a tag, restart buffer string
				s = "";
			} else {
				// append to buffer string
				s += c;
			}
		}
		
		// close HTML scanner
		fullHTMLScanner.close();
		
		// return the words
		return words;
	}
	
}
