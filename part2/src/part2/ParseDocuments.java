package part2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ParseDocuments {

	public static void main(String[] args) throws IOException {
		
		// validate arguments
		if (args.length < 2) {
			throw new IllegalArgumentException("Missing input and output folder command line arguments!");
		}
		
		// get folder containing downloaded html pages
		String inputFolder = args[0];
		
		// get output folder
		String outputFolder = args[1];
		
		// extract folder for plateau statistics
		String resultStatisticsFolder = args.length > 2 ? args[2] : null;
		
		/*
		 * Two step text area extraction!
		 *  1) NoiseReduction: remove known noise, such as ads.
		 *  2) Find the optimal plateau in the cumulative distribution graph between tokens and tags.
		 */
		NoiseReduction noiseReduction = new NoiseReduction();
		PlateauOptimization plateauOptimization = new PlateauOptimization();
		
		// noise reduction on each file in the input folder
		for (File f : (new File(inputFolder)).listFiles()) {
			if (!f.isFile()) continue;
			
			try {
				// feedback
				System.out.print("Processing: " + f.getName() + " - ");
				
				// Extract the html from the file
				String html = extractHTML(f);
				
				// reduce noise
				html = noiseReduction.processContent(html);
				
				// plateau optimization
				if (resultStatisticsFolder != null)
					plateauOptimization.setCSVFilepath(new File(resultStatisticsFolder, f.getName()+".csv").toString());
				html = plateauOptimization.processContent(html);
				
				// write the results
				writeResults(html, new File(outputFolder, f.getName()).toString());
				
				// feedback
				System.out.println("DONE");
			} catch (Exception e) {
				// feedback
				System.out.println("ERR");
			}
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
	 * Write the extracted html to file.
	 * @param html the extracted html
	 * @param filepath the filepath
	 * @throws IOException io exception
	 */
	public static void writeResults(String html, String filepath) throws IOException {
		FileWriter writer = new FileWriter(new File(filepath));
		writer.write(html);
		writer.close();
	}
	
}
