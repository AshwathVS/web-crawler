import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CrawlDataStore {

    private ConcurrentHashMap<String, Integer> visitedURLS = new ConcurrentHashMap<>(100);

    private ConcurrentLinkedQueue<String> urlsToVisit = new ConcurrentLinkedQueue<>();

    private Integer totalOccurrenceCount = 0;

    protected boolean hasURLBeenVisited(String url) {
        return visitedURLS.containsKey(url);
    }

    public void storeOccurrenceInURL(String url, int count) {
        this.visitedURLS.put(url, count);
        this.totalOccurrenceCount += count;
    }

    public synchronized void addUrlsToVisit(Collection<String> urlsToVisit) {
        for (String url : urlsToVisit) {

            // add the url to queue only if the url has not been visited yet
            if (!visitedURLS.containsKey(url)) {
                this.urlsToVisit.add(url);
            }
        }
    }

    public String getUrlToCrawl() {
        return urlsToVisit.poll();
    }

    public void printStatistics(String keyword) {
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

    public int getCurrentUrlsListCount() {
        return this.urlsToVisit.size();
    }

    public int getTotalWordCount() {
        return this.totalOccurrenceCount;
    }

    public int getTotalPagesVisitedCount() {
        return this.visitedURLS.size();
    }

}
