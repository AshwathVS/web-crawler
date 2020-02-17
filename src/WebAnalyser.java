import java.util.List;
import java.util.Map;

public class WebAnalyser implements Runnable, WebStatistics {

    public static int getCountOfUrlsToVisit() {
        return urlsToVisit.size();
    }

    private String keyword;

    public WebAnalyser(String keyword) {
        this.keyword = keyword;
    }

    private void addUrlToTouchedList(String url) {
        int existingCount = urlsTouchedCount.get(url);
        urlsTouchedCount.put(url, existingCount + 1);
    }

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
        addUrlToTouchedList(URL);

        // Check if url has been visited
        if (visitedURLS.containsKey(URL)) {
            System.out.println("Already visited URL: " + URL);
        } else {
            // adding a dummy value to the visited list
            visitedURLS.put(URL, 0);

            // crawling the url
            final String htmlContent = Helper.getContentFromURL(URL);
            List<String> hyperLinksInThePage = Helper.getHyperlinksFromContent(URL, htmlContent);
            urlsToVisit.addAll(hyperLinksInThePage);

            // store the number of occurrences
            int occurrenceCount = Helper.countNumberOfOccurrences(word, htmlContent);
            if (occurrenceCount > 0) {
                visitedURLS.put(URL, occurrenceCount);
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

        int totalOccurrenceCount = 0;
        System.out.println(String.format("Word: '%s' appears", keyword));

        for (Map.Entry<String, Integer> entry : visitedURLS.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.println(String.format("%d time(s) on %s", entry.getValue(), entry.getKey()));
                totalOccurrenceCount += entry.getValue();
            }
        }

        System.out.println("Crawling Information: ");
        System.out.println("Total Pages Crawled: " + visitedURLS.size());
        System.out.println("Total Occurrences: " + totalOccurrenceCount);
    }


    @Override
    public void run() {
        // TODO Please complete this method


    }


    public static void main(String[] args) {
        // TODO complete this method
    }


}
