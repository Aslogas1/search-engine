package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.repository.PageRepository;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final JsoupParser jsoupParser;

    public void saveAllPages(String url) {
        Set<String> children = jsoupParser.getChildren(url);
        List<String> path = children.stream()
                .map(s -> s.replaceAll(url, "/")).toList();
        for (String child : children) {
            Document doc;
            try {
                doc = Jsoup.connect(child)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Connection.Response response;
            try {
                response = doc.connection().execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Page page = new Page();
            page.setContent(doc.html());
            path.forEach(page::setPath);
            page.setCode(response.statusCode());
            pageRepository.save(page);
        }
    }
}
