package edu.noisereduction;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;

/**
 * Noise removal program from HTML page stored in local machine.
 *
 */
public class ReduceNoiseFromHtml 
{
	private static List<String> adList;
	
	static NoiseReductionHelper helper = new NoiseReductionHelper();

	public static void main( String[] args ) throws IOException
	{
		File htmlFolder = new File("HTML Files");
		File contentFolder = new File("Content Files");
		
		FileFilter htmlFilter = new FileFilter() {
			
			public boolean accept(File file) {
				String fileName = file.getAbsolutePath().toLowerCase();
				if(file.isFile() && (fileName.endsWith(".html") || fileName.endsWith(".htm"))) {
					return true;
				}
				return false;
			}
		};
		
		adList = helper.getAdListList();
		
		if(htmlFolder.exists() && htmlFolder.isDirectory()) {
			File[] htmlFiles = htmlFolder.listFiles(htmlFilter);
			for(int i = 0 ; i < htmlFiles.length ; i++) {
				FileWriter contentFile = new FileWriter(new File(contentFolder.getName()+"/"+htmlFiles[i].getName()+".txt"), false);
				System.out.println("Processing: "+htmlFiles[i].getName());
				contentFile.write(helper.extractContentDocument(Jsoup.parse(htmlFiles[i], "UTF-8", ""), adList));
				contentFile.close();
			}
		}
		System.out.println("Noise removal is done for all html files.");
	}

	



	
}