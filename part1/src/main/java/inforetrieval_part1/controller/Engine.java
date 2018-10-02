package inforetrieval_part1.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Engine
{
   final static int DEFAULT_NUM_THREADS = 10;
   final static String DEFAULT_SEED = "www.google.com";
   final static int DEFAULT_PAGES_TO_CRAWL = 1000;
   final static String DEFAULT_DOMAIN_RESTRICTION = null;
   private int numThreads;
   private String seed;
   private int numPagesToCrawl;
   private String domainRestriction;
   private boolean testMode;
   Set<String> doneSet = Collections.synchronizedSet(new HashSet<String>());
   LinkedBlockingQueue<String> todoList = new LinkedBlockingQueue<String>();
   Crawler[] crawlers;
   
   // Constructor
   public Engine() {
      this.numThreads = DEFAULT_NUM_THREADS;
      this.seed = DEFAULT_SEED; 
      this.numPagesToCrawl = DEFAULT_PAGES_TO_CRAWL;
      this.domainRestriction = DEFAULT_DOMAIN_RESTRICTION;
      this.testMode = false;
      this.initialize();
   }
   
   // Helper function
   private void initialize() {
      doneSet.clear();
      todoList.clear();
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
      this.domainRestriction = domainRestriction;
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
         while(this.doneSet.size() < this.numPagesToCrawl) {
            System.out.println("LOOP " + (loop++) + ", " +
                  this.doneSet.size() + "/" + this.numPagesToCrawl);
            if(!todoList.isEmpty()) {
               while(!todoList.isEmpty()) {
                  data = todoList.take();
                  crawlers[magicFunction(data) % this.numThreads].addToFrontier(data);
               }
            }
            Thread.sleep(1000);
         }
      } catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   /**
    * Algorithm to determine which thread should get which URL string.
    * @return
    */
   private int magicFunction(String str) {
      int sum = 0;
      for(int i=0; i<str.length(); i++) {
         sum += str.charAt(i);
      }
      return sum;
   }
   
   /**
    * Create and start all threads
    */
   public void createAllThreads(int numThreads) {
      if(numThreads<1)
         return;
      
      this.numThreads = numThreads;
      crawlers = new Crawler[this.numThreads];
      for(int i=0; i<crawlers.length; i++) {
         System.out.println("Creating thread "+i);
         crawlers[i] = new Crawler(doneSet, todoList);
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
}
