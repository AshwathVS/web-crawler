import java.util.Collections;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Crawl Manager will maintain a thread to urls to visit ratio of 3, if the ratio exceeds 3, then 'n' number
 * of threads such that the ratio is maintained will be spawned.
 */
public class CrawlManager {

    private static final int MAX_THREAD_NUM = 50;

    private static final int MAX_PAGE_COUNT = 200;

    private static final int MAX_WORDS_COUNT = 4000;

    private static final int BASE_URLS_TO_THREAD_RATIO = 3;

    private String baseUrl;

    private String keyword;

    private boolean crawlInitiated;

    private CrawlDataStore crawlDataStore;

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public CrawlManager(String baseUrl, String keyword) throws IllegalURLException {
        if (!Helper.isUrlValid(baseUrl)) {
            throw new IllegalURLException("Illegal URL: " +  baseUrl);
        }
        this.baseUrl = baseUrl;
        this.keyword = keyword;
        this.crawlInitiated = false;
        this.executor.setMaximumPoolSize(MAX_THREAD_NUM);
        this.crawlDataStore = new CrawlDataStore();
    }

    /**
     * Initiates crawling with the parameters specified in the constructor
     * @throws CrawlAlreadyInitiatedException
     */
    public void initiateCrawl() throws CrawlAlreadyInitiatedException {
        if (crawlInitiated) {
            throw new CrawlAlreadyInitiatedException("Crawling already initiated, cancel the current crawl to initiate a new crawl");
        } else {
            System.out.println("Crawl started, please wait...");
            crawlInitiated = true;
            this.crawlDataStore.addUrlsToVisit(Collections.singleton(this.baseUrl));
            initiateFreeThreadsBasedOnUrlsToVisit();
        }
    }

    /**
     * Starts triggering the threads
     * Is responsible for spawning new threads based on the number of urls present in the queue
     */
    private void initiateFreeThreadsBasedOnUrlsToVisit() {
        boolean continueCrawl = true;
        this.executor.submit(new WebAnalyser(this.keyword, this.crawlDataStore));

        while (continueCrawl) {

            int urlsToCrawlCount = this.crawlDataStore.getCurrentUrlsListCount();

            // If the ratio of urls to threads is greater than RATIO, we will spawn a new thread
            // creating new threads logic
            int activeThreadCount = executor.getActiveCount();
            if (activeThreadCount > 0 && (urlsToCrawlCount / activeThreadCount > BASE_URLS_TO_THREAD_RATIO)) {
                if (!maxThreadSizeReached()) {
                    int additionalNumberOfThreadsToSpawn = (urlsToCrawlCount / BASE_URLS_TO_THREAD_RATIO + 1) - executor.getActiveCount();
                    while (additionalNumberOfThreadsToSpawn > 0 && !maxThreadSizeReached()) {
                        executor.submit(new WebAnalyser(this.keyword, this.crawlDataStore));
                        additionalNumberOfThreadsToSpawn--;
                    }
                }
            }

            // submit available tasks till the executor takes more tasks
            while (executor.getActiveCount() < executor.getMaximumPoolSize() && this.crawlDataStore.getCurrentUrlsListCount() > 0) {
                try {
                    this.executor.submit(new WebAnalyser(keyword, this.crawlDataStore));
                } catch (RejectedExecutionException ex) {
                    // This is thrown when the executor cannot take any more tasks
                    break;
                }
            }

            continueCrawl = !continueCrawling();
            if (!continueCrawl) {
                System.err.println("Threshold conditions reached, exiting...");
            }
        }


        try {
            new WebAnalyser(this.keyword, this.crawlDataStore).printStatistics();
        } catch (Exception ex) {
            System.out.println("Error while printing stats...");
            ex.printStackTrace();
        }
        this.executor.shutdown();
    }

    private boolean maxThreadSizeReached() {
        return this.executor.getPoolSize() == this.executor.getMaximumPoolSize();
    }

    /**
     * Checks the threshold conditions and checks if we can continue crawling
     * @return
     */
    private boolean continueCrawling() {
        return
                        (this.crawlDataStore.getTotalPagesVisitedCount() >= MAX_PAGE_COUNT)
                ||
                        (this.crawlDataStore.getTotalWordCount()  >= MAX_WORDS_COUNT)
                ||
                        ((this.executor.getActiveCount() == 0 && this.crawlDataStore.getCurrentUrlsListCount() == 0));
    }
}
