import java.util.List;

public class WebAnalyser implements Runnable, WebStatistics {

    private String keyword;

    private CrawlDataStore crawlDataStore;

    private boolean statsThread;

    public WebAnalyser(String keyword, CrawlDataStore crawlDataStore) {
        this.keyword = keyword;
        this.crawlDataStore = crawlDataStore;

        // Stats thread will just print the stats and exit
        this.statsThread = false;
    }

    /**
     * This constructor will make the thread a stats thread
     * @param keyword
     * @param crawlDataStore
     * @param statsThread
     */
    public WebAnalyser(String keyword, CrawlDataStore crawlDataStore, boolean statsThread) {
        this.keyword = keyword;
        this.crawlDataStore = crawlDataStore;

        // Stats thread will just print the stats and exit
        this.statsThread = true;

    }

    //    private void addUrlToTouchedList(String url) {
//        int existingCount = urlsTouchedCount.get(url);
//        urlsTouchedCount.put(url, existingCount + 1);
//    }

    /**
     * find (keyword,URL) method should
     * <p>
     * (1) mark a URL 'visited' by adding it to the "visited" list
     * (2) if a given URL contains the chosen keyword
     * then the you should
     * (a) add this URL to result set and
     * (b) record how many times the keyword occurs on this page.
     * If the keyword does not occur then skip (a) and (b)
     * <p>
     * This method should skip URLs that have been visited by other threads
     *
     * @param word a string for case-insensitive keyword search
     * @param URL     starting from this seed page
     * @throws InterruptedException
     */
    @Override
    public void find(String word, String URL) throws InterruptedException {

        // Add to touched list (for debugging purpose to see how many threads have touched a single url)
        // addUrlToTouchedList(URL);

        // Check if url has been visited
        if (crawlDataStore.hasURLBeenVisited(URL)) {
//            System.out.println("Already visited URL: " + URL);
        } else {
            // adding a dummy value to the visited list
            crawlDataStore.storeOccurrenceInURL(URL, 0);
            System.out.println("Visiting URL: " + URL);
            // crawling the url
            final String htmlContent = Helper.getContentFromURL(URL);
            List<String> hyperLinksInThePage = Helper.getHyperlinksFromContent(URL, htmlContent);
            crawlDataStore.addUrlsToVisit(hyperLinksInThePage);

            // store the number of occurrences
            int occurrenceCount = Helper.countNumberOfOccurrences(word, htmlContent);
            if (occurrenceCount > 0) {
                crawlDataStore.storeOccurrenceInURL(URL, occurrenceCount);
            }
        }
    }


    /**
     * printStatistics()  :  used to print results
     * <p>
     * <p>
     * Sample output:
     * <p>
     * <p>
     * Word: 'Software' appears
     * <p>
     * 2 time(s) on http://www..../page1.html
     * 2 time(s) on http://www..../page1.1.html
     * 3 time(s) on http://www..../page1.2.html
     * ....
     * <p>
     * Total: 11 pages
     * 'Software' appears 43 time(s)
     * <p>
     * (results may vary depending on your crawling strategy)
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public void printStatistics() throws
            KeywordNotProvidedException, IllegalURLException {
        if (this.keyword == null) {
            throw new KeywordNotProvidedException("Keyword has not been provided");
        }
        crawlDataStore.printStatistics(this.keyword);
    }


    @Override
    public void run() {
        try {
            if (statsThread) {
                this.printStatistics();
            } else {
                String urlToCrawl = crawlDataStore.getUrlToCrawl();
                if (null == urlToCrawl) {
                    return;
                } else {
                    find(this.keyword, urlToCrawl);
                }
            }
        } catch (InterruptedException | KeywordNotProvidedException | IllegalURLException ex) {
            System.out.println("Specific Exception: " + ex.getLocalizedMessage());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getLocalizedMessage());
        }
    }


    public static void main(String[] args) {
        try {
            final String baseUrl = "https://www.stackoverflow.com";
            final String keyword = "java";
            CrawlManager crawlManager = new CrawlManager(baseUrl, keyword);
            crawlManager.initiateCrawl();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
