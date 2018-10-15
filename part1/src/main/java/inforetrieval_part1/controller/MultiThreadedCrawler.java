package inforetrieval_part1.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class MultiThreadedCrawler
{
    private final String REPOSITORY_DIR = "repository";
    private File reportHtml = new File("report.html");
    
    private final static int DEFAULT_NUM_THREADS = 10;
    private final static String DEFAULT_SEED = "www.google.com";
    private final static int DEFAULT_PAGES_TO_CRAWL = 1000;
    private final static String DEFAULT_DOMAIN_RESTRICTION = null;
    private int numThreads;
    private String seed;
    private int numPagesToCrawl;
    private String domainRestriction;
    private boolean testMode;
    Set<String> doneSet = Collections.synchronizedSet(new LinkedHashSet<String>());
    LinkedBlockingQueue<String> todoList = new LinkedBlockingQueue<String>();
    CrawlThread[] crawlers;
    private long startTime, elapsedTime;
    
    // Constructor
    public MultiThreadedCrawler() {
        this.numThreads = DEFAULT_NUM_THREADS;
        this.seed = DEFAULT_SEED; 
        this.numPagesToCrawl = DEFAULT_PAGES_TO_CRAWL;
        this.domainRestriction = DEFAULT_DOMAIN_RESTRICTION;
        this.testMode = false;
        this.startTime = System.currentTimeMillis();
        this.elapsedTime = this.startTime;
        doneSet.clear();
        todoList.clear();
    }
    
    public void execute(int numThreads) {
       System.out.println("Crawling information...");
       
        try {
           this.init();
           this.createAllThreads(numThreads);
           this.work();
           this.stopAllThreads();
           this.finishingTouches();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nFinished crawling!");
    }
    
    // Helper function
    public void init() throws IOException {
        File repoDir = new File(REPOSITORY_DIR);
        // If directory exists, delete all files inside it
        if (repoDir.exists()) {
            FileUtils.cleanDirectory(repoDir);
        }

        // Create repo folder and report file
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("static/baseReportHeader.html");
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, Charset.defaultCharset());
        is.close();
        String headerHtml = writer.toString();
        // Overwrite report.html if it already exists
        Files.write(Paths.get(reportHtml.getAbsolutePath()), headerHtml.getBytes());
    }
    
    // Getters and setters
    public int getDoneSetSize() {
        return this.doneSet.size();
    }
    
    public int getTodoListSize() {
        return this.todoList.size();
    }
    
    public void setSpecification(String seed, int numPagesToCrawl, 
            String domainRestriction) {
        this.seed = seed;
        todoList.add(this.seed);
        this.numPagesToCrawl = numPagesToCrawl;
        if (domainRestriction.equals("") || domainRestriction.equals(null)) {
           this.domainRestriction = null;
        }
        else {
           String protocol = "";
           try
           {
              protocol = new URL(seed).getProtocol();
             
           } catch (MalformedURLException e)
           {
              // TODO Auto-generated catch block
              e.printStackTrace();
           }
           
           this.domainRestriction = protocol + "://" + domainRestriction;
           System.out.println("Domain restriction = " + this.domainRestriction);
        }
        
    }
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    
    /**
     * Main engine loop, works to distribute URLs to crawling threads
     */
    public void work() {
        String data;
        int loop = 0;
        try
        {
            while(this.doneSet.size() < this.numPagesToCrawl+10) {
                elapsedTime = (System.currentTimeMillis() - this.startTime) / 1000;
                System.out.println("LOOP " + (loop++) + ", Done=" +
                        this.doneSet.size() + "/" + this.numPagesToCrawl + 
                        ", elapsed time " + elapsedTime + " sec");
                if(!todoList.isEmpty()) {
                    while(!todoList.isEmpty()) {
                        data = todoList.take();
                        crawlers[magicFunction(data) % this.numThreads].addToFrontier(data);
                    }
                    // Monitor crawler workloads
                    System.out.print("Threads("+this.crawlers.length+") frontier sizes=[");
                    for(int i=0; i<this.crawlers.length; i++) {
                       System.out.print(this.crawlers[i].getFrontierSize() + " ");
                    }
                    System.out.println("]");
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        elapsedTime = (System.currentTimeMillis() - this.startTime) / 1000;
        System.out.println("\nDone " + this.doneSet.size() + "/" + this.numPagesToCrawl 
              + " sites, elapsed time " + elapsedTime + " sec");
    }
    
    /**
     * Algorithm to determine which thread should get which URL string.
     * @return
     */
    private int magicFunction(String str) {
        return Math.abs(str.hashCode());
    }
    
    /**
     * Create and start all threads
     */
    public void createAllThreads(int numThreads) {
        if(numThreads<1)
            return;
        
        this.numThreads = numThreads;
        crawlers = new CrawlThread[this.numThreads];
        for(int i=0; i<crawlers.length; i++) {
            System.out.println("Creating thread "+i);
            crawlers[i] = new CrawlThread(doneSet, todoList, REPOSITORY_DIR, reportHtml, this.numPagesToCrawl);
            crawlers[i].setDomainRestriction(this.domainRestriction);
            crawlers[i].setTestMode(this.testMode);
            crawlers[i].startCrawler();
        }
    }
    
    /**
     * Stop all threads
     */
    public void stopAllThreads() {
        for(int i=0; i<crawlers.length; i++) {
            crawlers[i].stopCrawler();
        }
    }
    
    /**
     * Create sorted done list string 
     * @return string
     */
    public String toStringDoneSet() {
        String str = "";
        List<String> sortedList = this.doneSet.stream().collect(Collectors.toList());
        Collections.sort(sortedList);
        for (int i=0; i<sortedList.size(); i++) {
            str += sortedList.get(i) + "\n";
        }
        return str;
    }

    /**
     * Create sorted todo list string 
     * @return string
     */
    public String toStringTodoList() {
        String str = "";
        List<String> sortedList = this.todoList.stream().collect(Collectors.toList());
        Collections.sort(sortedList);
        for (int i=0; i<sortedList.size(); i++) {
            str += sortedList.get(i) + "\n";
        }
        return str;
    }
    
    /**
     * Finish up what needs to be done.
     * 1. Finish the footer of report.html
     * @throws IOException
     */
    public void finishingTouches() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("static/baseReportFooter.html");
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, Charset.defaultCharset());
        is.close();
        String footerHtml = writer.toString();
        Files.write(Paths.get(reportHtml.getAbsolutePath()), footerHtml.getBytes(),
                StandardOpenOption.APPEND);
    }
}
