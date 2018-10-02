package part2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
public class PlateauOptimization implements ContentProcessor {
	
	// the slope calculate delta as a ratio of the total number of tokens in a document
	private static final double DELTA_RATIO = 0.50;
	private static final double SLOPE_IMPROVEMENT = 0.05;
	private static final double MIN_TEXT_AREA_SIZE_RATIO = 0.1;
	private static final double SLOPE_IMPROVEMENT_EXTENSION = -0.05;
	private static final double INITIAL_BOUNDS_REMOVAL_RATIO = 0.1;
	
	public String processContent(File f) throws IOException {
		
		// extract the full HTML, without spaces, from the file
		String fullHTML = extractHTML(f);
		
		// HTML token and document statistics
		List<String> tokens = new ArrayList<>();
		List<Integer> tokenIdentifier = new ArrayList<>();
		List<Integer> tagCount = new ArrayList<>();
		List<Integer> tokenCount = new ArrayList<>();
		extractTokensAndStatistics(fullHTML, tokens, tokenIdentifier, tagCount, tokenCount);
		
		// write to CSV for testing
		//writeCSV(tokenIdentifier, tagCount, tokenCount, "test.csv");
		
		// get the bounds for the text area
		int[] initialBounds = new int[]{(int) (tokens.size() * INITIAL_BOUNDS_REMOVAL_RATIO),
				tokens.size() - 1 - (int) (tokens.size() * INITIAL_BOUNDS_REMOVAL_RATIO)};
		int[] bounds = getTextAreaBounds(tagCount, initialBounds);
		
		// print the bounds for testing
		System.out.println(Arrays.toString(bounds));
		
		// return the extracted text area from the document defined by the boundary points
		return extractTextArea(tokens, tokenIdentifier, bounds);
		
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
	 * Gets the text area boundary points
	 * @param tagCount the cumulative tag count
	 * @param currentBounds the current bounds for text area extraction to be used for recursive call
	 * @return the boundary indexes of the text area
	 */
	public static int[] getTextAreaBounds(List<Integer> tagCount, int[] currentBounds) {
		
		// basecase
		
		// get the number of tokens
		int numTokens = currentBounds[1] - currentBounds[0] + 1;
		
		// get the delta for the document
		double delta = numTokens * DELTA_RATIO;
		
		// current min slope and indexes
		int iMinSlope = currentBounds[0];
		int jMinSlope = iMinSlope + (int) delta;
		double minSlope = 1.0;
		
		// find the smallest slope
		int i = iMinSlope;
		int j = jMinSlope;
		while (j < currentBounds[1]) {
			// calculate the slope
			double slope = ((double) tagCount.get(j) - (double) tagCount.get(i)) / delta;
			
			// update if smallest
			if (slope < minSlope) {
				iMinSlope = i;
				jMinSlope = j;
				minSlope = slope;
				//System.out.println("OKAT");
			}
			
			// increment
			i += 1;
			j += 1;
			
		}
		
		// get the previous slope
		double prevSlope = ((double) tagCount.get(currentBounds[1]) - (double) tagCount.get(currentBounds[0])) / (currentBounds[1] - currentBounds[0]);
		
		// return if the slope didn't improve enough or too many tokens are removed
		if (prevSlope - minSlope < SLOPE_IMPROVEMENT
				|| ((double) numTokens) / tagCount.size() < MIN_TEXT_AREA_SIZE_RATIO) {
			
			// extend the boundaries to encompass points with minimal slope change
			return extendBounds(currentBounds[0], currentBounds[1], prevSlope, tagCount);
		}
		
		// try again with a smaller boundary
		return getTextAreaBounds(tagCount, new int[] {iMinSlope, jMinSlope});
	}
	
	/**
	 * Try and stretch the bounds to include more points that do not change the slope too much.
	 * @param i the current left bound
	 * @param j the current right bound
	 * @param slope the current slope
	 * @param tagCount the cumulative tag count
	 * @return the extend bounds
	 */
	public static int[] extendBounds(int i, int j, double slope, List<Integer> tagCount) {
		
		// new slope calculation
		double newSlope;
		
		// continuously try and stretch the bounds
		while (true) {
			
			// need improvement in either bound to continue
			boolean improvement = false;
			
			// try improve left bound
			if (i > 0) {
				newSlope = ((double) tagCount.get(j) - (double) tagCount.get(i-1)) / (j - (i-1));
				if (slope - newSlope >= SLOPE_IMPROVEMENT_EXTENSION) {
					i -= 1;
					improvement = true;
				}
			}
			
			// try improve right bound
			if (j < tagCount.size() - 1) {
				newSlope = ((double) tagCount.get(j+1) - (double) tagCount.get(i)) / ((j+1) - i);
				if (slope - newSlope >= SLOPE_IMPROVEMENT_EXTENSION) {
					j += 1;
					improvement = true;
				}
			}
			
			// no improve made, break
			if (!improvement) {
				break;
			}
		}
		
		// return stretched bounds
		return new int[] {i, j};
	}
	
	/**
	 * Extract the text area defined by the boundary indexes.
	 * @param tokens a list of tokens, tags and words
	 * @param bounds the boundary points
	 * @return the extracted text area
	 */
	public static String extractTextArea(List<String> tokens, List<Integer> tokenIdentifier, int[] bounds) {
		String textArea = "";
		for (int i = bounds[0]; i <= bounds[1] && i < tokens.size(); i ++) {
			textArea += tokens.get(i);
			// add space to separate words
			if (tokenIdentifier.get(i) == 0) {
				textArea += " ";
			}
		}
		return textArea;
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
