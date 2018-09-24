package inforetrieval_part1.controller;

import org.jsoup.*;

public class CrawlController implements Controller {
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
        // TODO
        // Call all the other functions
        System.out.println("You are in the execute() function.");
        for (String s : info) {
            System.out.println(s);
        }
    }
    
    
    public void crawl(String[] info) {
        // TODO Auto-generated method stub
        
    }
}