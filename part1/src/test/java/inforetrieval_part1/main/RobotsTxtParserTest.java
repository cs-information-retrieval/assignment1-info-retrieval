package inforetrieval_part1.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import inforetrieval_part1.controller.RobotsTxtParser;

public class RobotsTxtParserTest {
    RobotsTxtParser robotsTxtParser;
    
    public RobotsTxtParserTest() {
        robotsTxtParser = new RobotsTxtParser();
    }
    
    
    @Test
    public void testIsAllowed() {
        String allowUrl = "https://www.google.com/search/about";
        String allowUrl2 = "https://www.google.com/search/howsearchworks";
        String allowUrl3 = "https://www.google.com/books?*q=related:*";
        
        String noAllowUrl = "https://www.google.com/search";
        String noAllowUrl2 = "https://www.google.com/sdch";
        String noAllowUrl3 = "https://www.google.com/?hl=blah&";
        String noAllowUrl4 = "https://www.google.com/?hl=blah&blah2&gws_rd=ssl";
        String noAllowUrl5 = "https://www.google.com/books?blah3zoom=blah4";
        String noAllowUrlLeetcode = "https://leetcode.com/submissions";
        String noAllowUrlLeetcode2 = "https://leetcode.com/problems/blah/interpret_solution";
        
        Assertions.assertTrue(robotsTxtParser.isAllowed(allowUrl));
        Assertions.assertTrue(robotsTxtParser.isAllowed(allowUrl2));
        Assertions.assertTrue(robotsTxtParser.isAllowed(allowUrl3));
        
        Assertions.assertFalse(robotsTxtParser.isAllowed(noAllowUrl));
        Assertions.assertFalse(robotsTxtParser.isAllowed(noAllowUrl2));
        Assertions.assertFalse(robotsTxtParser.isAllowed(noAllowUrl3));
        Assertions.assertFalse(robotsTxtParser.isAllowed(noAllowUrl4));
        Assertions.assertFalse(robotsTxtParser.isAllowed(noAllowUrl5));
        Assertions.assertFalse(robotsTxtParser.isAllowed(noAllowUrlLeetcode));
        Assertions.assertFalse(robotsTxtParser.isAllowed(noAllowUrlLeetcode2));
    }
    
    
    @Test
    public void testBuildAllowDisallowList() {
        String robotsTxt = "User-agent: *\n" + 
                "Disallow:    /search\n" + 
                "Allow:     /search/about\n" + 
                "Allow: /search/static\n" +
                "Allow: /search/howsearchworks\n" +
                "Disallow: /search/static/*\n" +
                "Disallow:    /sdch\n" + 
                "Disallow: /groups\n" + 
                "Disallow:        /index.html?\n" + 
                "Disallow: /?\n" + 
                "Allow:/?hl=\n" + 
                "Disallow: /?hl=*&\n" + 
                "Allow: /?hl=*&gws_rd=ssl$\n" + 
                "Disallow: /?hl=*&*&gws_rd=ssl\n" + 
                "\n" + 
                "# Certain social media sites are whitelisted to allow crawlers to access page markup when links to google.com/imgres* are shared. To learn more, please contact images-robots-whitelist@google.com.\n" + 
                "User-agent: Twitterbot\n" + 
                "Allow: /imgres\n" + 
                "\n" + 
                "User-agent: facebookexternalhit\n" + 
                "Allow: /imgres\n" + 
                "\n" + 
                "Sitemap: http://www.gstatic.com/s2/sitemaps/profiles-sitemap.xml\n" + 
                "Sitemap: https://www.google.com/sitemap.xml";
        
        String allow = "/search/about\n" + 
                "/search/static\n" + 
                "/search/howsearchworks\n" + 
                "/?hl=\n" + 
                "/?hl=*&gws_rd=ssl$";
        ArrayList<String> allowList = new ArrayList<String>(Arrays.asList(allow.split("\n")));
        
        String disallow = "/search\n" +
                "/search/static/*\n" + 
                "/sdch\n" + 
                "/groups\n" + 
                "/index.html?\n" + 
                "/?\n" + 
                "/?hl=*&\n" + 
                "/?hl=*&*&gws_rd=ssl";
        ArrayList<String> disallowList = new ArrayList<String>(Arrays.asList(disallow.split("\n")));
        
        List<List<String>> results = robotsTxtParser.buildAllowDisallowList(robotsTxt);
        
        Assertions.assertEquals(allowList, results.get(0));
        Assertions.assertEquals(disallowList, results.get(1));
    }
}
