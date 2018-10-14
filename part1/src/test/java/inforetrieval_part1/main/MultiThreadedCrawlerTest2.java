package inforetrieval_part1.main;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import inforetrieval_part1.controller.MultiThreadedCrawler;

public class MultiThreadedCrawlerTest2
{

   @Test
   public void testEngine_execute_noDomainRestriction() {
      System.out.println("Search Engine Application\n");
      
      final int NUM_THREADS = 10;
      final String SEED = "https://en.wikipedia.org/wiki/Main_Page";
      final int  NUM_PAGES_TO_CRAWL = 10;
      final String DOMAIN_RESTRICTION = "";
      
      MultiThreadedCrawler mtc = new MultiThreadedCrawler();
      mtc.setSpecification(SEED, NUM_PAGES_TO_CRAWL, DOMAIN_RESTRICTION);
      mtc.execute(NUM_THREADS);
      
      System.out.println("\nProgram complete.");
   }
   
   @Test
   public void testEngine_execute_DomainRestriction() {
      System.out.println("Search Engine Application\n");
      
      final int NUM_THREADS = 20;
      final String SEED = "https://en.wikipedia.org/wiki/Main_Page";
      final int  NUM_PAGES_TO_CRAWL = 50;
      final String DOMAIN_RESTRICTION = "en.wikipedia.org";
      
      MultiThreadedCrawler mtc = new MultiThreadedCrawler();
      mtc.setSpecification(SEED, NUM_PAGES_TO_CRAWL, DOMAIN_RESTRICTION);
      mtc.execute(NUM_THREADS);
      
      System.out.println("\nProgram complete.");
   }

}
