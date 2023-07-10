package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@Component
public class JsoupParser extends RecursiveTask<Integer> {

    public Set<String> getChildren(String link) {
        Document doc;
        try {
            doc = Jsoup.connect(link).ignoreHttpErrors(true).maxBodySize(0).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc.select("a").stream()
                .parallel()
                .map(el -> el.attr("abs:href"))
                .filter(el -> !el.contains("#") && !el.contains("+"))
                .collect(Collectors.toSet());
    }

    public Document generateConnection(String child) {
        Document doc;
        try {
            doc = Jsoup.connect(child)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    @Override
    protected Integer compute() {
        return null;
    }
}
