package terminalH.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Named;
import java.io.IOException;

@Named
public class CrawlerUtils {
    public static String EMPTY = "";

    public static Document getRequest(String url, boolean followRedirects) throws IOException {
        Connection con = Jsoup.connect(url).ignoreContentType(true).followRedirects(followRedirects);
        return con.get();
    }

    public static Element getFirstElementByClass(Element e, String className) {
        return e.getElementsByClass(className).first();
    }

    public static boolean isElementExistByClass(Element e, String className) {
        return getFirstElementByClass(e, className) != null;
    }

    public static Elements getElementsByClass(Element e, String className) {
        return e.getElementsByClass(className);
    }

    public static Connection createConnection(String url) {
        return Jsoup.connect(url);
    }

    public static String extractUrl(Element e) {
        return e.select("a").first().absUrl("href");
    }
}