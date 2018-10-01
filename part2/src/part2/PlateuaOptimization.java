package part2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	
	// tag and word identifiers
	public static final int TAG_IDENTIFIER = 1;
	public static final int WORD_IDENTIFIER = 0;
	
	public String processContent(File f) throws FileNotFoundException {
		
		// extract the full html, without spaces, from the file
		Scanner fileScanner = new Scanner(f);
		String fullHTML = "";
		while (fileScanner.hasNextLine()) {
			fullHTML += fileScanner.nextLine();
		}
		fileScanner.close();
		
		// list of tokens: tags or words
		List<String> tokens = new LinkedList<>();
		
		// list denoting token is tag or word
		List<Integer> tokenIdentifier = new LinkedList<>();
		
		// string buffer for a tag of characters between tags
		String s = "";
		
		// scan the full html text, one character at a time
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
						tokenIdentifier.add(WORD_IDENTIFIER);
					}
				}
				s = c;
				
			} else if (c.equals(">")) {
				/*
				 * end of tag
				 * 1) add the tag
				 * 2) start a new string
				 */
				s += c;
				tokens.add(s);
				tokenIdentifier.add(TAG_IDENTIFIER);
				s = "";
			} else {
				s += c;
			}
		}
		fullHTMLScanner.close();
		for (String token : tokens) {
			System.out.println(token);
		}
		return "";
		
	}

}
