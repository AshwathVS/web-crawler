
/**
 * Interface WebStatistics
 */

public interface WebStatistics {

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

    public void find(String word, String URL)
            throws InterruptedException;

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
    public void printStatistics()
            throws KeywordNotProvidedException,
            IllegalURLException;


}
