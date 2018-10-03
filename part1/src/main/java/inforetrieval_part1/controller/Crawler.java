package inforetrieval_part1.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class Crawler implements Runnable {
    private Thread thread;
    private Set<String> doneSet;
    private LinkedBlockingQueue<String> todoList;
    private LinkedList<String> frontier;
    private boolean domainRestriction;
    private String restrictedDomain;
    private boolean testMode;
    final static int POLITENESS_SLEEP_TIME = 30*1000;
    
    // Constructor
    public Crawler(Set<String> doneSet, LinkedBlockingQueue<String> todoList) {
        this.doneSet = doneSet;
        this.todoList = todoList;
        this.frontier = new LinkedList<String>();
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
    
    
    public void run() {
        String data; // a URL
        String dataDomain; // host domain of a URL
        
        while(!Thread.interrupted()) {
            try
            {
                // Process next data
                if(!this.frontier.isEmpty()) {
                    data = this.frontier.pop();
                    dataDomain = this.getHostFromURL(data);
                    
                    // Handle if data is: in doneSet, domain restricted, or good-to-go
                    if(this.doneSet.contains(data)) {
                        System.out.println(Thread.currentThread().getName() + " XXX  " + data + " already done!");
                    }
                    else if(this.domainRestriction && !dataDomain.equals(this.restrictedDomain)) {
                        System.out.println(Thread.currentThread().getName() + " XXX  " + data+ " not in domain!");
                    }
                    else {
                        if(testMode == false){
                            System.out.println(Thread.currentThread().getName() 
                                    + " <--  " + data);
                            this.doRealCrawl(data);
                        }
                        else {
                            System.out.println(Thread.currentThread().getName() 
                                    + " <--  " + data);
                            this.doTestModeCrawl(data);
                        }
                        
                        // Politeness policy, wait before crawling more data
                        Thread.sleep(POLITENESS_SLEEP_TIME);
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
        // Crawl data
        // Add data to done set
        // Get array of links
        // Send any new links to the master to-do queue
    }
    
    private void doTestModeCrawl(String data) {
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
    
    private String getHostFromURL(String url) {
        String host = "";
        try
        {
            host = (new URL(url)).getHost();
        } catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return host;
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
