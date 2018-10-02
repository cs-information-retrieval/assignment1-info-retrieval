package part2;

import java.io.File;
import java.io.IOException;

public class ParseDocuments {

	public static void main(String[] args) throws IOException {
		
		ContentProcessor p = new PlateauOptimization();
		
		File folder = new File("./resources/test_html/");
		
		for (File f : folder.listFiles()) {
			if (!f.isFile()) continue;
			System.out.println(f.getName());
			String s = p.processContent(f);
			//break;
		}
		
		//System.out.println(p.processContent(new File("./resources/test_html/keras.io.html")));
		
	}
	
}
