package part2;

import java.io.File;
import java.io.IOException;

public class ParseDocuments {

	public static void main(String[] args) throws IOException {
		
		ContentProcessor p = new PlateuaOptimization();
		
		File folder = new File("./resources/test_html/");
		
		for (File f : folder.listFiles()) {
			if (!f.isFile()) continue;
			p.processContent(f);
		}
		
	}
	
}
