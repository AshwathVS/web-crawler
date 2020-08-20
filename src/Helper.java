import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    /* Regular expressions  */
    public static final Pattern link_pattern = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
    public static final Pattern href_pattern = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
    public static final Pattern url_pattern = Pattern.compile("http[s]?:\\/\\/(?:w{3}?.)?([^;:'#~]+)\\.(\\w{2,3})");

    public static boolean isUrlValid(String url) {
        if (null == url) {
            return false;
        } else {
            return url_pattern.matcher(url).find();
        }
    }

    /**
     * Method countNumberOfOccurrences returns how many time
     * a given word appears in a text (case-insensitive)
     *
     * @param word: a keyword
     * @param text
     * @return integer
     * <p>
     * Example:
     * <p>
     * countNumberOfOccurrences("i","I love leicester")
     * should return: 1
     * <p>
     * countNumberOfOccurrences("Wish","I wish to wish the wish you wish to wish, but if you wish the wish the witch wishes, I won't wish the wish you wish to wish")
     * should return: 11
     */
    public static int countNumberOfOccurrences(String word, String text) {

        Matcher m = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE).matcher(text);
        int matches = 0;
        while (m.find()) {
            matches++;
        }
        return matches;
    }


    /**
     * getContentFromURL method returns the HTML source code of a given web page
     *
     * @param URLstring: URL (address) of a specific website or file on the Internet e.g. http://www.google.com
     * @return HTML source of the given URL
     */
    public static String getContentFromURL(String URLstring) {

        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try {
            url = new URL(URLstring);
            URLConnection urlConnection = url.openConnection();
            is = urlConnection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            StringBuffer sb = new StringBuffer();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            String content = sb.toString();
            return content;

        } catch (MalformedURLException mue) {
            System.out.println(mue.toString());
            // mue.printStackTrace();
        } catch (IOException ioe) {
            // System.out.println(ioe.toString());
            // ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                //  ioe.printStackTrace();
                System.out.println(ioe.toString());
            }
        }
        return "";

    }

    /**
     * getHyperlinksFromContent() method extracts all internal
     * hyperlinks from the HTML.
     * e.g. Suppose http://example.com/some.html contains the HTML source below:
     *
     * <HTML>
     * <b>A sample page with 4 links</b>
     * <a href="1.html">link1</a>
     * <a href="http://example.com/other.html">
     * <a href="http://www.google.com">
     * <a href="http://example.com/other.html#bookmark">
     * </HTML>
     * <p>
     * getHyperlinksFromContent("http://example.com/some.html", html_source)
     * will return
     * <p>
     * http://example.com/1.html
     * http://example.com/other.html
     * <p>
     * Note: (1) http://www.google.com
     * will not be included in the result because it belongs to a different domain
     * (2) http://example.com/other.html#bookmark
     * is not included because it is a bookmark
     *
     * @param URLstring: URL (address) of a specific web site or file
     *                   e.g. http://www.google.com
     * @param content:   HTML source code
     * @return A list of internal hyperlinks on this page (same domain as URLstring)
     */

    public static ArrayList<String> getHyperlinksFromContent(String URLstring, String content) {

        ArrayList<String> ret = new ArrayList<String>();

        try {
            URL url = new URL(URLstring);
            Matcher matcher = link_pattern.matcher(content);

            while (matcher.find()) {

                String links = matcher.group(1);
                Matcher hrefs = href_pattern.matcher(links);

                while (hrefs.find()) {
                    String link = hrefs.group(1).replaceAll("'", "").replaceAll("\"", "");
                    String absolutePath = "";
                    String path = url.getPath();
                    if ((path == null) || path.equals("") || path.equals("/")) {
                        absolutePath = "";
                    }
                    int lastSlashPos = path.lastIndexOf('/');
                    if (lastSlashPos >= 0) {
                        absolutePath = path.substring(0, lastSlashPos); //strip off the slash
                    } else {
                        absolutePath = "";
                    }

                    String address = "";
                    URI uri = new URI(link);
                    if (!uri.isAbsolute()) {
                        address = url.getProtocol() + "://" + url.getHost() + absolutePath + "/" + link;

                        if (!address.contains("#")) {
                            ret.add(address);
                        }

                    } else {
                        if (link.contains(url.getHost())) {
                            if (!link.contains("mailto:")) {
                                address = link;
                                if (!address.contains("#")) {
                                    ret.add(address);
                                }

                            }
                        }
                    }
                }

            }

        } catch (URISyntaxException e) {
            //System.out.println("Ignore mailto"+ e.toString());
        } catch (Exception ex) {
            //System.out.println(ex.toString());
        }

        return ret;

    }


    public static void main(String args[]) {
        String websiteURL = "https://www.freshworks.com/";
        String keyword = "London";

        String content = Helper.getContentFromURL(websiteURL);
        System.out.println(content);
        ArrayList<String> urls = Helper.getHyperlinksFromContent(websiteURL, content);
        System.out.println(urls.toString());

        System.out.println("The word '" + keyword + "' appears " + Helper.countNumberOfOccurrences(keyword, content) + " time(s) on this page");

    }


}

