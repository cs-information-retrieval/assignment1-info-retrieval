package part2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Attempts to extract the main content from a web page by
 *  finding the plateau in the cumulative distribution graph
 *  of the number of tokens vs the number of tags, as described:
 *  
 *  Finn, A., Kushmerick, N., & Smith, B. (2001) Fact or fiction:
 *  Content classification for digital libraries. In DELOS workshop:
 *  Personalisation and recommender systems in digital libraries.
 *  
 * @author jlepere2
 * @date 09/28/2018
 */
public class PlateuaOptimization implements ContentProcessor {
	
	public String processContent(File f) throws IOException {
		
		// extract the full HTML, without spaces, from the file
		String fullHTML = extractHTML(f);
		
		// HTML token and document statistics
		List<String> tokens = new LinkedList<>();
		List<Integer> tokenIdentifier = new LinkedList<>();
		List<Integer> tagCount = new LinkedList<>();
		List<Integer> tokenCount = new LinkedList<>();
		extractTokensAndStatistics(fullHTML, tokens, tokenIdentifier, tagCount, tokenCount);
		
		// write to CSV for testing
		writeCSV(tokenIdentifier, tagCount, tokenCount, "test.csv");
		
		// return the extracted text area from the document
		return extractTextArea(tokens, tokenIdentifier, tagCount, tokenCount);
		
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
	 * Extracts a list of documents and relevant document statistics from the HTML.
	 * @param fullHTML the full HTML string
	 * @param tokens an empty list for the tokens
	 * @param tokenIdentifier an empty list for identifying the tokens as tag or word
	 * @param tagCount an empty list for the cumulative tag count
	 * @param tokenCount an empty list for the cumulative token
	 */
	public static void extractTokensAndStatistics(String fullHTML, List<String> tokens, List<Integer> tokenIdentifier, List<Integer> tagCount, List<Integer> tokenCount) {
		
		// current tag count
		int currentTagCount = 0;
		
		// string buffer for a tag of characters between tags
		String s = "";
		
		// scan the full HTML text, one character at a time
		Scanner fullHTMLScanner = new Scanner(fullHTML);
		fullHTMLScanner.useDelimiter("");
		while (fullHTMLScanner.hasNext()) {
			
			// get the next character
			String c = fullHTMLScanner.next();
			
			if (c.equals("<")) {
				/*
				 * start of tag
				 * 1) add each word that was found in between the previous tag and this tag
				 * 2) start new tag string
				 */
				for (String word : s.trim().split(" ")) {
					if (!word.isEmpty()) {
						tokens.add(word);
						tokenIdentifier.add(0);
						tagCount.add(currentTagCount);
						tokenCount.add(tokenCount.size() + 1);
					}
				}
				s = c;
				
			} else if (c.equals(">")) {
				/*
				 * end of tag
				 * 1) add the tag
				 * 2) start a new string
				 */
				currentTagCount ++;
				s += c;
				tokens.add(s);
				tokenIdentifier.add(1);
				tagCount.add(currentTagCount);
				tokenCount.add(tokenCount.size() + 1);
				s = "";
			} else {
				s += c;
			}
		}
		
		// close HTML scanner
		fullHTMLScanner.close();
		
	}
	
	/**
	 * Extracts the relevant text area from the document.
	 * @param tokens a list of tokens
	 * @param tokenIdentifier a list identifying the token as a tag or word
	 * @param tagCount the cumulative tag count
	 * @param tokenCount the cumulative token count
	 * @return the extracted text area
	 */
	public static String extractTextArea(List<String> tokens, List<Integer> tokenIdentifier, List<Integer> tagCount, List<Integer> tokenCount) {
		
		// TODO
		return "";
	}
	
	/**
	 * Writes the results to a csv for testing, mainly to plot the tag count vs token count cdf.
	 * @param tokenIdentifier the list of token identifiers
	 * @param tagCount the cumulative distribution count of tags
	 * @param tokenCount the cumulative distribution of all tokens
	 * @param filepath the full path of the file to write
	 * @throws IOException io exception
	 */
	public static void writeCSV(List<Integer> tokenIdentifier, List<Integer> tagCount, List<Integer> tokenCount, String filepath) throws IOException {
		FileWriter testWriter = new FileWriter(new File(filepath));
		Iterator<Integer> tokenIdentifierIterator = tokenIdentifier.iterator();
		Iterator<Integer> tagCountIterator = tagCount.iterator();
		Iterator<Integer> tokenCountIterator = tokenCount.iterator();
		while (tokenIdentifierIterator.hasNext()) {
			testWriter.write(tokenIdentifierIterator.next()
					+ "," + tokenCountIterator.next()
					+ "," + tagCountIterator.next() + "\n");
		}
		testWriter.close();
	}

}
