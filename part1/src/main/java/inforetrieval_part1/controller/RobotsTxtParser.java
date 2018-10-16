package inforetrieval_part1.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class RobotsTxtParser
{
    /**
     * Check if URL is allowed in site robots.txt
     * @param urlString URL to check
     * @return true if URL is allowed
     */
    public boolean isAllowed(String urlString) {
        String host;
        String file;
        String protocol;
        
        urlString = this.checkHttpPrefix(urlString);
        
        String robotsTxtLink = "";
        boolean linkAllowed = true;
        
        try
        {
            URL url = new URL(urlString);
            protocol = url.getProtocol();
            host = url.getHost();
            file = url.getFile();
            
            robotsTxtLink = protocol + "://" + host + "/robots.txt";
            Connection.Response response = null;
            response = Jsoup.connect(robotsTxtLink).execute();
            
            // Reference:
            // https://stackoverflow.com/a/5445161
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.bodyStream(), writer);
            String robotsTxtContent = writer.toString();
            
            List<List<String>> results = buildAllowDisallowList(robotsTxtContent);
            List<String> allowList = results.get(0);
            List<String> disallowList = results.get(1);
            
            // Matches the link with disallowList and allowList to see if the link is allowed 
            linkAllowed = this.regexUrlAllowed(file, allowList, disallowList);
        
        } catch (MalformedURLException e1) {
            System.out.println("Malformed URL: " + urlString);
        } catch (IOException e2) {
            System.out.println("Did not get robots.txt: " + robotsTxtLink);
        }
        
        return linkAllowed;
    }
    
    
    public List<List<String>> buildAllowDisallowList(String robotsTxtContent) {
        String[] robotsContent = robotsTxtContent.split("\n");
        boolean userAgentFound = false;
        List<String> allowList = new ArrayList<String>();
        List<String> disallowList = new ArrayList<String>();
        
        for (String s : robotsContent) {
            if (userAgentFound) {
                String allowPatternStr = "^Allow:\\s*";
                Pattern allowPattern = Pattern.compile(allowPatternStr);
                Matcher allowMatcher = allowPattern.matcher(s);
                
                String disallowPatternStr = "^Disallow:\\s*";
                Pattern disallowPattern = Pattern.compile(disallowPatternStr);
                Matcher disallowMatcher = disallowPattern.matcher(s);
                
                // Check if the string starts with Allow:
                if (allowMatcher.find()) {
                    // Obtain only the URL
                    String temp = allowMatcher.replaceFirst("");
                    allowList.add(temp);
                }
                // Check if the string starts with Disallow:
                else if (disallowMatcher.find()) {
                    // Obtain only the URL
                    String temp = disallowMatcher.replaceFirst("");
                    disallowList.add(temp);
                }
                // If neither is found
                else {
                    userAgentFound = false;
                }
            }
            
            if (s.matches("User-agent:\\s*\\*")) {
                userAgentFound = true;
            }
        }
        
        List<List<String>> returnList = new ArrayList<List<String>>(2);
        returnList.add(allowList);
        returnList.add(disallowList);
        
        return returnList;
    }
    
    
    public boolean regexUrlAllowed(String urlPath, List<String> allowList,
            List<String> disallowList) {
        boolean urlAllowed = true;
        
        // Check the disallowList first
        for (String disallow : disallowList) {
            // First we have to replace * with \S* to look for all
            // Since we are in Java, we have to use \\S for our pattern string
            // non-whitespace characters
            if (disallow.contains("*")) {
                // Everything between \Q and \E will be treated as a literal string
                // Thus, if we start with xx*yy, we will end up with 
                // \Qxx\E\S*\Qyy\E
                
                // If the disallow string ends in an asterisk, we will add a 
                // non-whitespace match-all regex at the end of our pattern
                boolean endsWithAsterisk = disallow.endsWith("*");
                
                String[] tempSplit = disallow.split("\\*");
                String tempStr = "";
                for (int i = 0; i < tempSplit.length; i++) {
                    // Before the last split
                    if (i < tempSplit.length - 1) {
                        tempStr += "\\Q" + tempSplit[i] + "\\E\\S*";
                    }
                    // Last split
                    else {
                        tempStr += "\\Q" + tempSplit[i] + "\\E";
                    }
                }
                if (endsWithAsterisk)
                    tempStr = tempStr + "\\S*";
                disallow =  tempStr;
            }
            // Else, we are going to interpret the string literally
            else {
                disallow = "\\Q" + disallow + "\\E";
            }
            
            // If the url path matches the regex in the disallow list
            // NOTE that the disallow list should be the regex pattern, i.e.
            // it should be passed as a parameter
            Pattern p = Pattern.compile(disallow);
            Matcher m = p.matcher(urlPath);
            
            if (m.matches()) {
                urlAllowed = false;
                break;
            }
        }
        
        // Check the allowList
        for (String allow : allowList) {
            if (allow.contains("*")) {
                // Everything between \Q and \E will be treated as a literal string
                // Thus, if we start with xx*yy, we will end up with 
                // \Qxx\E\S*\Qyy\E
                
                boolean endsWithAsterisk = allow.endsWith("*");
                
                String[] tempSplit = allow.split("\\*");
                String tempStr = "";
                for (int i = 0; i < tempSplit.length; i++) {
                    // Before the last split
                    if (i < tempSplit.length - 1) {
                        tempStr += "\\Q" + tempSplit[i] + "\\E\\S*";
                    }
                    // Last split
                    else {
                        tempStr += "\\Q" + tempSplit[i] + "\\E";
                    }
                }
                if (endsWithAsterisk)
                    tempStr = tempStr + "\\S*";
                
                allow = tempStr;
            }
            else {
                allow = "\\Q" + allow + "\\E";
            }
            
            Pattern p = Pattern.compile(allow);
            Matcher m = p.matcher(urlPath);
            
            if (m.matches()) {
                urlAllowed = true;
                break;
            }
        }
        
        return urlAllowed;
    }
    
    /**
     * Check if the string begins with either "http://" or "https://".
     * If it doesn't, add "http" to the front
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
}
