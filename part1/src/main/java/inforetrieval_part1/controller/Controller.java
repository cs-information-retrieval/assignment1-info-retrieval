package inforetrieval_part1.controller;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Interface for the main controller class
 * @author ToddNguyen
 *
 */
public interface Controller {
    public void execute(String[] info);
    public void crawl(String[] info) throws IOException, InterruptedException, URISyntaxException;
}
