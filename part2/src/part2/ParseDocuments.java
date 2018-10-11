package part2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ParseDocuments {

	public static void main(String[] args) throws IOException {
		
		NoiseReduction noiseReduction = new NoiseReduction();
		PlateauOptimization plateauOptimization = new PlateauOptimization();
		
		String resultStatisticsFolder = "./resources/plateau_optimization_results/";
		
		File folder = new File("./resources/test_html/");
		
		for (File f : folder.listFiles()) {
			if (!f.isFile()) continue;
			System.out.println(f.getName());
			
			// Extract the html from the file
			String html = extractHTML(f);
			
			// reduce noise
			html = noiseReduction.processContent(html);
			
			// plateau optimization
			plateauOptimization.setCSVFilepath(resultStatisticsFolder+f.getName()+".csv");
			html = plateauOptimization.processContent(html);
			
			System.out.println(html.length());
			//break;
		}
		
		//System.out.println(p.processContent(new File("./resources/test_html/keras.io.html")));
		
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
	
}
