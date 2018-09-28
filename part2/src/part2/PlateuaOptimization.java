package part2;

import java.io.File;
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
	
	public String processContent(File f) throws IOException {
		Scanner scanner = new Scanner(f);
		List<String> tokens = new LinkedList<>();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			for (String s : line.split(" ")) {
				s = s.trim();
				if (!s.isEmpty()) {
					tokens.add(s);
				}
			}
		}
		scanner.close();
		for (String token : tokens) {
			System.out.println(token);
		}
		return "";
	}

}
