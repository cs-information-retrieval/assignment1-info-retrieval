package inforetrieval_part1.main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import inforetrieval_part1.controller.MultiThreadedCrawler;

public class MultiThreadedCrawlerTest {
    private MultiThreadedCrawler crawlController = MultiThreadedCrawler.getInstance();
    
    @Test
    public void testAddBetweenTd() {
        String input = "blah";
        String input2 = String.valueOf(2);
        
        String correctInput = "\t\t<td>" + input + "</td>";
        String correctInput2 = "\t\t<td>" + input2 + "</td>";
        
        Assertions.assertEquals(crawlController.addBetweenTd(input), correctInput);
        Assertions.assertEquals(crawlController.addBetweenTd(input2), correctInput2);
    }
    
    
    @Test
    public void testCheckHttpPrefix() {
        String hasHttp = "http://facebook.com";
        String hasHttps = "https://facebook.com";
        String noHttp = "facebook.com";
        String noHttpWithWww = "www.facebook.com";
        
        Assertions.assertEquals(crawlController.checkHttpPrefix(hasHttp), hasHttp);
        Assertions.assertEquals(crawlController.checkHttpPrefix(hasHttps), hasHttps);
        Assertions.assertEquals(crawlController.checkHttpPrefix(noHttp), "http://" + noHttp);
        Assertions.assertEquals(crawlController.checkHttpPrefix(noHttpWithWww), "http://" + noHttpWithWww);
    }
    
    
    @Test
    public void testDeleteUrlPrefix() {
        String http = "http://facebook.com";
        String https = "https://facebook.com";
        String www = "www.facebook.com";
        String correctString = "facebook.com";
        
        Assertions.assertEquals(crawlController.deleteUrlPrefix(http), correctString);
        Assertions.assertEquals(crawlController.deleteUrlPrefix(https), correctString);
        Assertions.assertEquals(crawlController.deleteUrlPrefix(www), correctString);
    }
}
