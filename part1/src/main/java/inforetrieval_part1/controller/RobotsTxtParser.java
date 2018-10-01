package inforetrieval_part1.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.panforge.robotstxt.RobotsTxt;

public class RobotsTxtParser
{
   /**
    * Check if URL is allowed in site robots.txt
    * @param url URL to check
    * @return true if URL is allowed
    */
   public boolean isAllowed(String url) {
      String host;
      String file;
      String protocol;
      
      try
      {
         protocol = new URL(url).getProtocol();
         host = new URL(url).getHost();
         file = new URL(url).getFile();
         InputStream in = new URL(protocol + "://" + host + "/robots.txt").openStream();
         RobotsTxt robotsTxt = RobotsTxt.read(in);
         //System.out.println(robotsTxt.toString());
         List<String> list = robotsTxt.getDisallowList(null);
         in.close();
         
         return !list.contains(file);
      } catch (MalformedURLException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
         System.err.println("Malformed URL: " + url);
      } catch (IOException e2) {
         // TODO Auto-generated catch block
         e2.printStackTrace();
      }
      return false;
   }
}
