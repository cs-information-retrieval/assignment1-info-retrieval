package inforetrieval_part1.main;

import inforetrieval_part1.controller.Engine;
import inforetrieval_part1.controller.RobotsTxtParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    public void testRobotsTxtParser_RealWebsiteWithRobotsTxt()
    {
       System.out.println("Starting testRobotsTxtParser on real website with robots.txt:");
       
       RobotsTxtParser rtp = new RobotsTxtParser();
       String[] trueurls = {
             "https://www.google.com",
             //"https://www.google.com/search/about",
             //"https://www.google.com/?hl=*&gws_rd=ssl$",
             //"https://www.google.com/?gws_rd=ssl$",
       };
       for(int i=0; i<trueurls.length; i++){
          assertTrue(rtp.isAllowed(trueurls[i]));
          //System.out.println(rtp.isAllowed(trueurls[i]) + " " + trueurls[i]);
       }
       
       String[] falseurls = {
             "https://www.google.com/search",
             //"https://www.google.com/?",
             //"https://www.google.com/index.html?",
             //"https://www.google.com/?hl=*&*&gws_rd=ssl",
             //"https://www.google.com/u/",
       };
       for(int i=0; i<falseurls.length; i++){
          assertFalse(rtp.isAllowed(falseurls[i]));
          //System.out.println(rtp.isAllowed(falseurls[i]) + " " + falseurls[i]);
       }
       
       System.out.println("Finished testRobotsTxtParser");
    }
    
    public void testRobotsTxtParser_NoRobotsTxt()
    {
       System.out.println("Starting testRobotsTxtParser_NoRobotsTxt on real website with NO robots.txt:");
       
       RobotsTxtParser rtp = new RobotsTxtParser();
       
       String[] noroboturls = {
             "https://www.google.com/m/robots.txt",
       };
       for(int i=0; i<noroboturls.length; i++){
          assertTrue(rtp.isAllowed(noroboturls[i]));
          //System.out.println(rtp.isAllowed(noroboturls[i]) + " " + noroboturls[i]);
       }
       
       System.out.println("Finished testRobotsTxtParser_NoRobotsTxt");
    }
    
    public void testEngine_NumCrawlPages_withoutDomainRestriction(){
       System.out.println("Search Engine Application\n");
       
       final int NUM_THREADS = 10;
       final String SEED = "https://www.google.com";
       final int  NUM_PAGES_TO_CRAWL = 3;
       final String DOMAIN_RESTRICTION = null;
       
       Engine engine = new Engine();
       engine.setSpecification(SEED, NUM_PAGES_TO_CRAWL, DOMAIN_RESTRICTION);
       engine.setTestMode(true);
       engine.createAllThreads(NUM_THREADS);
       
       System.out.println("DONESET["+engine.getDoneSetSize() + "]");
       System.out.println(engine.toStringDoneSet());
       System.out.println("TODOLIST["+engine.getTodoListSize()+"]");
       System.out.println(engine.toStringTodoList());
       
       engine.work();
       engine.stopAllThreads();
       
       System.out.println("DONESET["+engine.getDoneSetSize() + "]");
       System.out.println(engine.toStringDoneSet());
       System.out.println("TODOLIST["+engine.getTodoListSize()+"]");
       System.out.println(engine.toStringTodoList());
       
       System.out.println("\nProgram complete.");
       
       assertTrue(NUM_PAGES_TO_CRAWL <= engine.getDoneSetSize());
    }
    
    public void testEngine_NumCrawlPages_withDomainRestriction(){
       System.out.println("Search Engine Application\n");
       
       final int NUM_THREADS = 1;
       final String SEED = "http://www.google.com";
       final int  NUM_PAGES_TO_CRAWL = 5;
       final String DOMAIN_RESTRICTION = "www.google.com";
       
       Engine engine = new Engine();
       engine.setSpecification(SEED, NUM_PAGES_TO_CRAWL, DOMAIN_RESTRICTION);
       engine.setTestMode(true);
       engine.createAllThreads(NUM_THREADS);
       
       System.out.println("DONESET["+engine.getDoneSetSize() + "]");
       System.out.println(engine.toStringDoneSet());
       System.out.println("TODOLIST["+engine.getTodoListSize()+"]");
       System.out.println(engine.toStringTodoList());
       
       engine.work();
       engine.stopAllThreads();
       
       System.out.println("DONESET["+engine.getDoneSetSize() + "]");
       System.out.println(engine.toStringDoneSet());
       System.out.println("TODOLIST["+engine.getTodoListSize()+"]");
       System.out.println(engine.toStringTodoList());
       
       System.out.println("\nProgram complete.");
       
       assertTrue(NUM_PAGES_TO_CRAWL <= engine.getDoneSetSize());
    }
}
