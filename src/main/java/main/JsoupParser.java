package main;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsoupParser {

    private static final String URL = "http://www.playback.ru/";
    private static final String ATTR = "abs:href";
    private static final String AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";

    public static Set<String> getChildren(String link) throws IOException {
        Document doc = Jsoup.connect(link).ignoreHttpErrors(true).maxBodySize(0).get();
        return doc.select("a").stream()
                .parallel()
                .map(el -> el.attr(ATTR))
                .filter(el -> !el.contains("#") && !el.contains("+"))
                .collect(Collectors.toSet());
    }

    public static List<Integer> getStatusCode() throws IOException {
        Set<String> children = getChildren(URL);
        List<Integer> status = new ArrayList<>();
        int statusCode;
        for (String child : children) {
            Document doc = Jsoup.connect(child)
                    .userAgent(AGENT)
                    .referrer("http://www.google.com")
                    .get();
            Connection.Response response = doc.connection().execute();
            statusCode = response.statusCode();
            status.add(statusCode);
        }
        return status;
    }

    public static List<String> getContent() throws IOException {
        Set<String> children = getChildren(URL);
        List<String> content = new ArrayList<>();
        String html;
        for (String child : children) {
            Document doc = Jsoup.connect(child)
                    .maxBodySize(0)
                    .userAgent(AGENT)
                    .referrer("http://www.google.com")
                    .get();
            html = doc.html();
            content.add(html);
        }
        return content;
    }
}
