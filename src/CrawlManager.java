import java.util.Collections;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Crawl Manager will maintain a thread to urls to visit ratio of 3, if the ratio exceeds 3, then 'n' number
 * of threads such that the ratio is maintained will be spawned.
 */
public class CrawlManager {

    private static final int MAX_THREAD_NUM = 20;

    private static final int MAX_PAGE_COUNT = 200;

    private static final int MAX_WORDS_COUNT = 4000;

    private static final int TIME_OUT = 10000;

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

    public void initiateCrawl() throws CrawlAlreadyInitiatedException {
        if (crawlInitiated) {
            throw new CrawlAlreadyInitiatedException("Crawling already initiated, cancel the current crawl to initiate a new crawl");
        } else {
            crawlInitiated = true;
            this.crawlDataStore.addUrlsToVisit(Collections.singleton(this.baseUrl));
            initiateFreeThreadsBasedOnUrlsToVisit();
        }
    }

    private void initiateFreeThreadsBasedOnUrlsToVisit() {
        Date crawlStartTime = new Date();
        boolean continueCrawl = true;
        this.executor.submit(new WebAnalyser(this.keyword, this.crawlDataStore));

        while (continueCrawl) {

            int urlsToCrawlCount = this.crawlDataStore.getCurrentUrlsListCount();
            // If the ratio of urls to threads is greater than RATIO, we will spawn a new thread
            if (executor.getActiveCount() > 0 && (urlsToCrawlCount / executor.getActiveCount() > BASE_URLS_TO_THREAD_RATIO)) {
                if (!maxThreadSizeReached()) {
                    int additionalNumberOfThreadsToSpawn = (urlsToCrawlCount / BASE_URLS_TO_THREAD_RATIO + 1) - executor.getActiveCount();
                    while (additionalNumberOfThreadsToSpawn > 0 && !maxThreadSizeReached()) {
                        executor.submit(new WebAnalyser(this.keyword, this.crawlDataStore));
                        additionalNumberOfThreadsToSpawn--;
                    }
                }
            }

            // trigger available threads
            // if the task count to thread pool ratio is greater than 2, we will stop submitting tasks
            while (executor.getActiveCount() < executor.getMaximumPoolSize()) {
                try {
                    this.executor.submit(new WebAnalyser(keyword, this.crawlDataStore));
                } catch (RejectedExecutionException ex) {
                    // This is thrown when the executor cannot take any more tasks
                    break;
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            if (this.crawlDataStore.getTotalPagesVisitedCount() >= 100) {
                continueCrawl = false;
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

    private void updateCrawlDetails() {

    }
}
