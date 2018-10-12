package inforetrieval_part1.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class CrawlController implements Controller {
    private final String REPOSITORY_DIR = "repository";
    private File reportHtml = new File("report.html");

    private LinkedList<String> frontier = new LinkedList<String>();
    private HashSet<String> visitedList = new HashSet<String>();
    private int crawlDelay;  // in seconds
    private int minSecondsToWait = 15;  // in seconds
    private int maxSecondsToWait = 25;  // in seconds
    private Random random = new Random();  // randomly generate seconds to wait each time

    // Getters and setters
    public LinkedList<String> getFrontier() {return this.frontier;}
    public HashSet<String> visitedList() {return this.visitedList;}

    // End Variable Declaration + getters/setters
    //*************************************************************************

    // Make this a Singleton
    private static CrawlController instance;
    private CrawlController() {};
    public static CrawlController getInstance() {
        if (instance == null) {
            instance = new CrawlController();
        }
        return instance;
    }

    public void execute(String[] info) {
        System.out.println("Crawling information...");

        try {
            this.init();
            this.crawl(info);
            this.finishingTouches();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finished crawling!");
    }


    /**
     * 1. Clear both frontier and visited list
     * 2. Delete all files in the "repository" folder. Also generate a new report.html file.
     * @throws IOException
     */
    private void init() throws IOException {
        frontier.clear();
        visitedList.clear();

        File repoDir = new File(REPOSITORY_DIR);
        // If directory exists, delete all files inside it
        if (repoDir.exists()) {
            FileUtils.cleanDirectory(repoDir);
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("static/baseReportHeader.html");
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, Charset.defaultCharset());
        is.close();
        String headerHtml = writer.toString();
        // Overwrite report.html if it already exists
        Files.write(Paths.get(reportHtml.getAbsolutePath()), headerHtml.getBytes());
    }


    /**
     * Finish up what needs to be done.
     * 1. Finish the footer of report.html
     * @throws IOException
     */
    private void finishingTouches() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("static/baseReportFooter.html");
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, Charset.defaultCharset());
        is.close();
        String footerHtml = writer.toString();
        Files.write(Paths.get(reportHtml.getAbsolutePath()), footerHtml.getBytes(),
                StandardOpenOption.APPEND);
    }


    /**
     * Crawl the web
     * @param info -
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException 
     */
    public void crawl(String[] info) throws IOException, InterruptedException {
        // TODO:
        // 1. Check robots.txt

        String seedUrl = info[0];
        int maxPages = Integer.parseInt(info[1]);
        String domainRestriction = info[2];
        
        // Add https to domainRestriction if it is not already there
        if (domainRestriction.contains("http") == false) {
            domainRestriction = "http://" + domainRestriction;
        }

        // Add the seed URL to frontier
        frontier.add(seedUrl);

        // While the frontier is not empty AND while we haven't reached the max number of pages yet
        while (frontier.isEmpty() == false && visitedList.size() < maxPages) {
            String currentUrl = frontier.poll();
            
            // Convert the string to a URI. This is used to check only the domain, not the prefix
            // "http" or "www.", and verify that it's not in the visited list.
            // However, Jsoup can only connect to domains beginning with http
            // so we will keep the original string
            String domainOnlyUrl = this.deleteUrlPrefix(currentUrl);
            
            // Check if the link begins with "http://" or "https://"
            // Jsoup can only connect to websites that begin with the above-mentioned prefix
            currentUrl = this.checkHttpPrefix(currentUrl);
            
            // Only crawl if domain restriction is met
            if (this.checkDomainRestriction(currentUrl, domainRestriction)) {
                // Only crawl if the url is not in the visited list
                if (visitedList.contains(domainOnlyUrl) == false) {
                    // Add to the visited list
                    visitedList.add(domainOnlyUrl);
    
                    // Actually crawl the page
                    ArrayList<String> foundLinks = this.crawlOnePage(currentUrl);
    
                    // Put links in the frontier; double check if it is not already in visited list
                    for (String link : foundLinks) {
                        String linkUriDomainOnly = this.deleteUrlPrefix(link);
                        
                        if (visitedList.contains(linkUriDomainOnly) == false) {
                            frontier.add(link);
                        }
                    }
                    System.out.println(visitedList.size() + ". " + currentUrl);
                }
            }
        }
    }
    
    
    /**
     * Check if the current link is within the domain restriction
     * @param domainInput - The current link being looked at
     * @param domainRestriction - The domain restriction
     * @return
     * @throws MalformedURLException 
     */
    public boolean checkDomainRestriction(String domainInput, String domainRestriction) throws MalformedURLException {
        boolean validSite = false;
        
        // Take out the prefix ("http", "https", "www") for domain restriction
        String noPrefixDomainRestriction = this.deleteUrlPrefix(domainRestriction).trim();
        
        // If the domain restriction is empty then all sites are allowed
        if (noPrefixDomainRestriction.isEmpty()) {
            validSite = true;
        }
        // Domain restriction is not empty
        else {
            String hostInputUrl = new URL(domainInput).getHost().replace("www.", "");
            String hostRestrictionUrl = new URL(domainRestriction).getHost().replace("www.", "");
            
            // Compare the two hosts
            validSite = hostInputUrl.equalsIgnoreCase(hostRestrictionUrl);
        }
        
        return validSite;
    }


    /**
     * Helper function to get all links from a url
     * @param url - The url to crawl
     * @return An ArrayList of all crawled URLs
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException 
     */
    private ArrayList<String> crawlOnePage(String url) throws IOException, InterruptedException {
        ArrayList<String> foundLinks = new ArrayList<String>();
        
        Connection.Response response = null;
        int statusCode = 404;  // default is not found
        Document doc = null;
        int numberOfNonvisitedLinks = 0;
        int totalNumberOfLinks = 0;
        
        // Try to connect to the url. If the URL cannot be connected due to whatever reason,
        // our program will print out an error message and continue to crawl
        try {
            response = Jsoup.connect(url)
                    .ignoreHttpErrors(true)
                    .execute();
            statusCode = response.statusCode();
        } catch (IOException e) {
            System.out.println(url + " could not be crawled");
        }
        

        // If it is a successful connection
        if (statusCode == 200) {
            doc = response.parse();

            // Export the document to a repository
            exportToRepository(doc);

            // Select all anchor links
            Elements links = doc.select("a");
            // Extract only the link
            for (Element link : links) {
                String abs_href = link.attr("abs:href").trim();
                // We only care about links that are not null
                if (abs_href.equals("") == false) {
                    // Increment the total number of links
                    totalNumberOfLinks++;

                    // Eliminate any # as they just lead to the same page
                    abs_href = abs_href.split("#")[0];
                    
                    // Check if the link begins with "http://" or "https://"
                    // Jsoup can only connect to websites that begin with the above-mentioned prefix
                    abs_href = this.checkHttpPrefix(abs_href);
                    
                    // Convert to domain only to check in visited list
                    String abs_href_domain = this.deleteUrlPrefix(abs_href);

                    // Only add links that are not in the visited list
                    if (this.visitedList.contains(abs_href_domain) == false) {
                        foundLinks.add(abs_href);
                        numberOfNonvisitedLinks++;
                    }
                }
            }

            // CrawlDelay will be a random integer from min-max seconds
            crawlDelay = random.nextInt(maxSecondsToWait - minSecondsToWait) + minSecondsToWait;
            
            // Wait crawlDelay seconds and then continue to crawl
            TimeUnit.SECONDS.sleep(crawlDelay);
        }

        // Generate a report for this website
        this.generateReportHtml(statusCode, doc, numberOfNonvisitedLinks, totalNumberOfLinks);
        return foundLinks;
    }


    /**
     * Export the current webpage to a repository folder
     * @param doc - The web document
     * @throws IOException
     */
    private void exportToRepository(Document doc) throws IOException {
        File repoDir = new File(REPOSITORY_DIR);
        repoDir.mkdirs(); // make directory if it doesn't exist
        // Filename will be doc0, where 0 is the current size of visited list - 1
        String fileName = "doc" + (this.visitedList.size() - 1) + ".html";
        // Join the paths together
        Path path = Paths.get(repoDir.getAbsolutePath(), fileName);
        Files.write(path, doc.toString().getBytes());
    }


    /**
     * Generate a report html
     * @param statusCode - The status code of the connection
     * @param doc - The web document
     * @param numberOfNonvisitedLinks - Number of non-visited non-empty links on the current page.
     * @param totalNumberOfLinks - Total number of non-empty links
     * @throws IOException
     */
    private void generateReportHtml(int statusCode, Document doc,
            int numberOfNonvisitedLinks, int totalNumberOfLinks) throws IOException {
        String currentDir = System.getProperty("user.dir");

        int pageId = this.visitedList.size() - 1;
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
        output.add(addBetweenTd(String.valueOf(numberOfNonvisitedLinks)));
        output.add(addBetweenTd(String.valueOf(totalNumberOfLinks)));
        output.add(addBetweenTd(String.valueOf(numberOfImages)));
        output.add("\t\t</tr>");
        output.add("\n");

        Files.write(Paths.get("report.html"), output, StandardOpenOption.APPEND);
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
     * Check if the string begins with either "http://" or "https://"
     * @param url - URL to check
     * @return
     */
    public String checkHttpPrefix(String url) {
        String strPattern = "(?:^https:\\/\\/|^http:\\/\\/)([\\S]*)";
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(url);
        
        // If the string does not start with "https://" or "http://"
        if (matcher.matches() == false) {
            url = "http://" + url;
        }
        
        return url;
    }
    
    
    /**
     * Delete http://, https://, and any www at the start of a URL.
     * @param url - The current url to look at
     * @return
     */
    public String deleteUrlPrefix(String url) {
        String strPattern = "";
        Pattern pattern;
        Matcher matcher;
        
        // If string begins with "https://" or "http://"
        // Group 0 - (?:^https:\\/\\/|^http:\\/\\/): Do not capture this group, matches beginning with https:// or http://
        // Group 1 - ([\\S]*): Matches all non-whitespace character following group 0
        strPattern = "(?:^https:\\/\\/|^http:\\/\\/)([\\S]*)";
        pattern = Pattern.compile(strPattern);
        matcher = pattern.matcher(url);
        // Found a match
        if (matcher.find()) {
            url = matcher.group(1);
        }
                
        
        // If newly modified string begins with "www."
        // Group 0 - (?:^www.): non-capturing group, matches beginning www.
        // Group 1 - ([\\S]*): any non-whitespace character after the begining www.
        strPattern = "(?:^www.)([\\S]*)";
        pattern = Pattern.compile(strPattern);
        matcher = pattern.matcher(url);
        // Found a match
        if (matcher.find()) {
            url = matcher.group(1);
        }
        
        // If the newly modified string ends with a "/" (forward slash) then delete that slash
        strPattern = "([\\S]*)/$";
        pattern = Pattern.compile(strPattern);
        matcher = pattern.matcher(url);
        // Found a match
        if (matcher.find()) {
            url = matcher.group(1);
        }
                
        return url;
    }
}
