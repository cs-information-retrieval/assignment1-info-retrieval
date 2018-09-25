package inforetrieval_part1.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlController implements Controller {
    
	private LinkedList<String> frontier = new LinkedList<String>();
    private HashSet<String> visitedList = new HashSet<String>();
    private int crawlDelay = 15; // in seconds
    
    // Getters and setters
    public LinkedList<String> getFrontier() {return this.frontier;}
    public HashSet<String> visitedList() {return this.visitedList;}
    
    // End Variable Declaration + getters/setters
    //*************************************************************************
    
    // Make this a Singleton
    private static CrawlController instance;
    private CrawlController() {};
    public static CrawlController getInstance() {
        if (instance == null) {
            instance = new CrawlController();
        }
        return instance;
    }
    
    public void execute(String[] info) {
    	System.out.println("Crawling information...");
    	
        try {
			crawl(info);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        System.out.println("Finished crawling!");
    }
    
    
    /**
     * Crawl the web
     * @param info - 
     * @throws IOException 
     * @throws InterruptedException 
     */
    public void crawl(String[] info) throws IOException, InterruptedException {
    	// TODO:
    	// 1. Check robots.txt
    	// 2. Check domain restriction
    	
    	String seedUrl = info[0];
    	int maxPages = Integer.parseInt(info[1]);
    	String domainRestriction = info[2];
    	
    	// Add the seed URL to frontier
    	frontier.add(seedUrl);
    	
    	// While the frontier is not empty AND while we haven't reached the max number of pages yet
    	while (frontier.isEmpty() == false && visitedList.size() < maxPages) {
    		String currentUrl = frontier.poll();
    		// Only crawl if the url is not in the visited list
    		if (visitedList.contains(currentUrl) == false) {
    			// Add to the visited list
    			visitedList.add(currentUrl);
    			
    			// Crawl the page
    			ArrayList<String> foundLinks = this.getLinks(currentUrl);
    			// Put links in the frontier; double check if it is not already in visited list
    			for (String link : foundLinks) {
    				if (visitedList.contains(link) == false) {
    					frontier.add(link);
    				}
    			}
    			
    			System.out.println(visitedList.size() + ". " + currentUrl);
    		}
    		// Wait crawlDelay seconds and then continue to crawl
			TimeUnit.SECONDS.sleep(crawlDelay);
    	}
    	
    	// DEBUG
    	BufferedWriter writer = new BufferedWriter(new FileWriter("output_links.txt"));
    	writer.write("Frontier:");
    	int count = 1;
    	while (frontier.isEmpty() == false && count < 25) {
    		String currentUrl = frontier.poll();
    		writer.write(count + ". " + currentUrl + "\n");
    		count++;
    	}
    	writer.write("\n======================");
    	writer.write("Visited List:");
    	Iterator<String> iter = visitedList.iterator();
    	count = 1;
    	while (iter.hasNext()) {
    		String currentUrl = iter.next();
    		writer.write(count + ". " + currentUrl + "\n");
    		count++;
    	}
    	writer.close();
    }
    
    
    /**
     * Helper function to get all links from a url
     * @param url - The url to crawl
     * @return An ArrayList of all crawled URLs
     * @throws IOException 
     */
    private ArrayList<String> getLinks(String url) throws IOException {
    	ArrayList<String> foundLinks = new ArrayList<String>();
    	Document doc = Jsoup.connect(url).get();
    	// Select all anchor links
    	Elements links = doc.select("a");
    	// Extract only the link
    	for (Element link : links) {
    		String abs_href = link.attr("abs:href").trim();
    		// We only care about links that are not null
    		if (abs_href.equals("") == false) {
    			// Only add links that are not in the visited list
    			if (this.visitedList.contains(abs_href) == false) {
    				foundLinks.add(abs_href);
    			}
    		}
    	}
    	
//    	for (String s : foundLinks) {
//    		System.out.println(s);
//    	}
    	
    	return foundLinks;
    }
}
