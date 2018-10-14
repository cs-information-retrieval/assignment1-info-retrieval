package inforetrieval_part1.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class CrawlThread implements Runnable {
    private Thread thread;
    private Set<String> doneSet;
    private LinkedBlockingQueue<String> todoList;
    private String folder;
    private File file;
    private LinkedList<String> frontier;
    private boolean domainRestriction;
    private String restrictedDomain;
    private boolean testMode;
    final static int POLITENESS_FIXED_DELAY = 30;           // in seconds
    final static int POLITENESS_MIN_DELAY = 15;  // in seconds
    final static int POLITENESS_MAX_DELAY = 25;  // in seconds
    
    private CrawlOne scraper = new CrawlOne();
    private RobotsTxtParser robot = new RobotsTxtParser();
    
    // Constructor
    public CrawlThread(Set<String> doneSet, LinkedBlockingQueue<String> todoList, 
          String folder , File file) {
        this.doneSet = doneSet;
        this.todoList = todoList;
        this.frontier = new LinkedList<String>();
        this.folder = folder;
        this.file = file;
        this.domainRestriction = false;
        this.testMode = false;
    }
    
    // Getters and setters
    public void setDomainRestriction(String restrictedDomain) {
        this.restrictedDomain = restrictedDomain;
        this.domainRestriction = (restrictedDomain == null) ? false : true;
    }
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    
    // Add frontier data to this thread local queue
    public void addToFrontier(String data) {
        this.frontier.add(data);
    }
    // Get frontier size
    public int getFrontierSize() {
        return this.frontier.size();
    }
    
    public void run() {
        String data; // a URL
        
        while(!Thread.interrupted()) {
            URL currentURL = null;
            String currentDomain = "";
            try
            {
                // Process next data
                if(!this.frontier.isEmpty()) {
                    data = this.frontier.pop();
                    try
                    {
                       currentURL = new URL(data);
                       currentDomain = currentURL.getProtocol() + "://" + currentURL.getHost();
                    } catch (MalformedURLException e)
                    {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                    }
                    
                    // Handle if data is: in doneSet, domain restricted, or good-to-go
                    if(this.doneSet.contains(data)) {
                        // Skip, nothing to crawl!
                        //System.out.println(Thread.currentThread().getName() + " XXX  " + data + " already done!");
                    }
                    else if(this.domainRestriction && !currentDomain.equals(this.restrictedDomain)) {
                       System.out.println(currentDomain + " != " + this.restrictedDomain);
                       //System.out.println(Thread.currentThread().getName() + " XXX  " + data+ " not in domain!");
                    }
                    else if(!robot.isAllowed(data)) {
                        System.out.println("**Robot blocked " + data);
                    }
                    else {
                        if(testMode == false){
                            //System.out.println(Thread.currentThread().getName() 
                            //        + " <--  " + data);
                            this.doRealCrawl(data);
                        }
                        else {
                            //System.out.println(Thread.currentThread().getName() 
                            //        + " <--  " + data);
                            this.doTestModeCrawl(data);
                        }
                        
                        // Politeness policy, wait before crawling more data
                        //this.scraper.sleepRandomTime(POLITENESS_FIXED_DELAY);
                        this.scraper.sleepRandomTime(POLITENESS_MIN_DELAY, POLITENESS_MAX_DELAY);
                    }
                }
                else {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e)
            {
                break;
            }
        }
    }
    
    private void doRealCrawl(String data) {
        ArrayList<URL> foundLinks = new ArrayList<URL>();
        
        
        try
        {
           // Crawl data and get array of links
           foundLinks = this.scraper.crawlOnePage(data);
           
           // Add current URL to done set
           this.doneSet.add(data);
           
           // Save report and repository to files
           int index = getSetIndex(data);
           this.scraper.exportToRepository(index, this.folder);
           this.scraper.generateReportHtml(index, this.file.toString());
           
           // Send found links to the master to-do queue
           for(URL link : foundLinks) {
              if (link.getAuthority() == null) continue;
              if (this.domainRestriction && link.getAuthority().equals(this.restrictedDomain)) {
                 //System.out.println(Thread.currentThread().getName() + "  --> " + link.toString());
                 this.todoList.add(link.toString());
              }
              else {
                 //System.out.println(Thread.currentThread().getName() + "  --> " + link.toString());
                 this.todoList.add(link.toString());
              }
           }
        } catch (InterruptedException e1)
        {
           System.out.println("InterruptedException crawling " + data);
           e1.printStackTrace();
        } catch (IOException e1)
        {
           System.out.println("IOException writing " + this.file.toString());
           e1.printStackTrace();
        }
    }
    
    private int getSetIndex(String value) {
       int result = 0;
       Iterator<String> iter = this.doneSet.iterator();
       
       while(iter.hasNext()) {
          if(iter.next().equals(value)) return result;
          result++;
       }
       return -1;
    }
    
    private void doTestModeCrawl(String data) {
        System.out.println("Doing a test mode crawl!"); 
        String newLink;
        
        // Crawl data (nothing to do)
        
        // Add data to done set
        this.doneSet.add(data);
        
        // Get array of links
        // Send any new data to the master to-do queue
        // If domain restriction, add links within domain, else add random domains
        if (this.domainRestriction) {
            for(int i=0; i<2; i++) {
                newLink = randomFile(5);
                System.out.println(Thread.currentThread().getName() + "  --> " + newLink);
                this.todoList.add(newLink);
            }
        }
        else {
            for(int i=0; i<2; i++) {
                newLink = randomDomain(5);
                System.out.println(Thread.currentThread().getName() + "  --> " + newLink);
                this.todoList.add(newLink);
            }
        }
    }
    
    private String randomDomain(int size) {
        final String alpha = "abcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        String str = "";
        for(int i=0; i<size; i++) {
            int index = rnd.nextInt(alpha.length());
            str += alpha.substring(index, index+1);
        }
        return "https://www." + str + ".com";
    }
    private String randomFile(int size) {
        final String alpha = "abcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        String str = "https://"+this.restrictedDomain;
        for(int i=0; i<size; i++) {
            int index = rnd.nextInt(alpha.length());
            str += "/" + alpha.substring(index, index+1);
        }
        return str;
    }
    /**
     * Create and start a new thread
     */
    public void startCrawler() {
        this.thread = new Thread(this);
        this.thread.start();
    }
    
    /**
     * Stop the running thread
     */
    public void stopCrawler() {
        this.thread.interrupt();
        System.out.println("Interrupted " + this.thread.getName());
        try
        {
            this.thread.join();
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
