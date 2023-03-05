package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JsoupParser {

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
}
