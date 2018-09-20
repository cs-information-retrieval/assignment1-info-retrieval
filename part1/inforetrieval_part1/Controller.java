package inforetrieval_part1;

import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ToddNguyen
 */
public class Controller {
    // Make it a Singleton
    public static Controller instance;
    private Controller() {};
    
    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        
        return instance;
    }
    
    
    /**
     * Main function to execute reading of file and start to web crawl information
     * @param input - Seed, max number of pages to crawl, and domain restriction
     */
    public void execute(String[] input) {
        for (String s : input) {
            System.out.println(s);
        }
    }
}
