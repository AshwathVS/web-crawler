import java.util.Date;
import java.util.concurrent.*;

public class CrawlManager {
    private static int MAX_THREAD_NUM = 20;

    private static int MAX_PAGE_COUNT = 200;

    private static int MAX_WORDS_COUNT = 4000;

    private static int TIME_OUT = 10000;

    private Date crawlStartTime;

    private Date crawlEndTime;

    private String baseUrl;

    private boolean crawlInitiated;

    private boolean crawlCompleted;

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public CrawlManager(String baseUrl) throws IllegalURLException {
        if (!Helper.isUrlValid(baseUrl)) {
            throw new IllegalURLException("Illegal URL: " +  baseUrl);
        }
        this.baseUrl = baseUrl;
        this.crawlInitiated = false;
        this.crawlCompleted = false;
    }

//    public CrawlManager(String baseUrl, int maxThreadCount)

    public void initiateCrawl() throws CrawlAlreadyInitiatedException {
        if (crawlInitiated) {
            throw new CrawlAlreadyInitiatedException("Crawling already initiated, cancel the current crawl to initiate a new crawl");
        } else {
            crawlStartTime = new Date();
            crawlInitiated = true;
        }
    }

    public void initiateFreeThreadsBasedOn() {
        // check for free threads

        // if no free threads, add threads until it reaches the MAX_THREAD_NUM

        // if free threads are available, make use of them
    }
}
