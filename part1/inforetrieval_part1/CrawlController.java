package inforetrieval_part1;

public class CrawlController implements Controller {
    // Make this a Singleton
    private static CrawlController instance;
    private CrawlController() {};
    public static CrawlController getInstance() {
        if (instance == null) {
            instance = new CrawlController();
        }
        return instance;
    }
    @Override
    public void execute(String[] info) {
        // TODO
        // Call all the other functions
        System.out.println("You are in the execute() function.");
        for (String s : info) {
            System.out.println(s);
        }
    }
    @Override
    public void crawl() {
        // TODO Auto-generated method stub
        
    }
}
