package inforetrieval_part1;

public class Controller {
    // Singleton
    private static Controller instance;
    private Controller() {};
    
    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }
    
    
    /**
     * Main function to crawl
     * @param args - The seed, max number of pages to crawl, and domain restriction
     */
    public void execute(String[] args) {
        //TODO
        // Call all the other functions
        System.out.println("You are in execute() function");
    }
}
