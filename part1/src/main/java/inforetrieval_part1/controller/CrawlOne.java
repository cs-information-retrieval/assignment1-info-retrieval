package inforetrieval_part1.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlOne
{
   ArrayList<URL> foundLinks = new ArrayList<URL>();
   
   private int statusCode;
   private Response response;
   private Document doc;
   
   private Random random = new Random();  // randomly generate seconds to wait
   
   // Constructor
   public CrawlOne() {
      this.init();
   }
   
   private void init() {
      this.foundLinks.clear();
      this.response = null;
      this.doc = null;
      this.statusCode = 404;
   }
   
   /**
    * Helper function to get all links from a url
    * @param url - The url to crawl
    * @return An ArrayList of all crawled URLs
    * @throws IOException
    * @throws InterruptedException
    * @throws URISyntaxException 
    */
   public ArrayList<URL> crawlOnePage(String url) throws InterruptedException, IOException {
       this.init();
       
       // Try to connect to the url. If the URL cannot be connected, 
       // our program will print out an error message and continue to crawl.
       try {
           this.response = Jsoup.connect(url)
                   .ignoreHttpErrors(true)
                   .execute();
           this.statusCode = this.response.statusCode();
       } catch (IOException e) {
           System.out.println(url + " could not be crawled");
       }
       
       // If it is a successful connection
       if (this.statusCode == 200) {
           this.doc = this.response.parse();
           
           // Select all anchor links
           Elements links = doc.select("a");
           
           // Extract only the link
           for (Element link : links) {
               String abs_href = link.attr("abs:href").trim().split("#")[0];
               try {
                  URL foundUrl = new URL(abs_href);
                  this.foundLinks.add(foundUrl);
               } catch (MalformedURLException e) {
                  //System.out.println(abs_href + " gave MalformedURLException");
               }
           }
       }
       return this.foundLinks;
   }

   public void sleepRandomTime(int delay) throws InterruptedException {
      // Wait crawlDelay seconds and then continue to crawl
      TimeUnit.SECONDS.sleep(delay);
   }
   
   public void sleepRandomTime(int min, int max) throws InterruptedException {
      // CrawlDelay will be a random integer from min-max seconds
      int crawlDelay = random.nextInt(max - min) + min;
      
      // Wait crawlDelay seconds and then continue to crawl
      TimeUnit.SECONDS.sleep(crawlDelay);
   }
   

   /**
    * Generate a report html
    * @param statusCode - The status code of the connection
    * @param doc - The web document
    * @param numberOfNonvisitedLinks - Number of non-visited non-empty links on the current page.
    * @param totalNumberOfLinks - Total number of non-empty links
    * @throws IOException
    */
   public void generateReportHtml(int index, String reportFileName) {
       String currentDir = System.getProperty("user.dir");

       int pageId = index;
       String title = "N/A";
       String clickableUrl = "N/A";
       String linkToDownloadedPage = "N/A";
       int httpStatusCode = statusCode;
       int numberOfImages = 0;

       // Check if the website is successfully crawled
       if (doc != null) {
           // Max length of link
           int maxLinkLength = 20;
           int maxDownloadedPageLength = maxLinkLength + 10;

           // Obtain the title from the webpage
           title = doc.title();

           clickableUrl = "<a href=\"";
           String clickableUrlTemp = doc.location();
           clickableUrl += (clickableUrlTemp + "\">" + clickableUrlTemp + "</a>");

           linkToDownloadedPage = "<a href=\"file:///";
           String linkTemp = Paths.get(currentDir, "repository", "doc" + pageId + ".html").toString();
           int linkTempSize = linkTemp.length();
           if (linkTempSize < maxDownloadedPageLength) {
               linkToDownloadedPage += (linkTemp + "\">" + linkTemp + "</a>");
           } else {
               linkToDownloadedPage += (linkTemp + "\">..."
                       + linkTemp.substring(linkTempSize - maxDownloadedPageLength, linkTempSize)
                       + "</a>");
           }
           
           numberOfImages = doc.select("img").size();
       }
       
       // Append to report.html
       ArrayList<String> output = new ArrayList<String>();
       output.add("\t<tr>");
       output.add("\t\t<th scope=\"row\">" + pageId + "</th>");
       output.add(addBetweenTd(title));
       output.add(addBetweenTd(clickableUrl));
       output.add(addBetweenTd(linkToDownloadedPage));
       output.add(addBetweenTd(String.valueOf(httpStatusCode)));
       output.add(addBetweenTd(String.valueOf(0)));
       output.add(addBetweenTd(String.valueOf(this.foundLinks.size())));
       output.add(addBetweenTd(String.valueOf(numberOfImages)));
       output.add("\t\t</tr>");
       output.add("\n");
       
       try
       {
          Files.write(Paths.get(reportFileName), output, StandardOpenOption.APPEND);
       } catch (ClosedByInterruptException e1)
       {
          System.out.println("ClosedByInterruptException " + reportFileName);
       } catch (IOException e2) {
          
       }
   }
   
   /**
    * Add text between td tags
    * @param input - The input between the table data tags
    * @return
    */
   private String addBetweenTd(String input) {
       String htmlCode = "\t\t<td>";
       htmlCode += input;
       htmlCode += "</td>";
       return htmlCode;
   }
   
   /**
    * Export the current webpage to a repository folder
    * @param doc - The web document
    * @throws IOException
    */
    public void exportToRepository(int index, String repositoryDir) throws IOException {
        File repoDir = new File(repositoryDir);
        repoDir.mkdirs(); // make directory if it doesn't exist
        
        // Filename will be doc0, where 0 is the current size of visited list - 1
        String fileName = "doc" + index + ".html";
        
        // Join the paths together
        Path path = Paths.get(repoDir.getAbsolutePath(), fileName);
        if(this.doc == null){
           Files.write(path, "".getBytes());
        }
        else {
           Files.write(path, this.doc.toString().getBytes());
        }
    }
}

