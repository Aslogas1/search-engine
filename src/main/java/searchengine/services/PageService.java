package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final JsoupParser jsoupParser;
    private final SiteService siteService;
    private final LemmaService lemmaService;
    private final SearchIndexService searchIndexService;


    public void saveAllPages(String url) {
        Set<String> children = jsoupParser.getChildren(url);
        Site site = siteService.saveSite(url);
        try {
            List<String> path = children.stream()
                    .map(s -> s.replaceAll(url, "")).toList();
            for (String child : children) {
                Document doc = jsoupParser.generateConnection(child);
                Connection.Response response;
                try {
                    response = doc.connection().execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Page page = new Page();
                page.setSiteId(site.getId());
                page.setContent(doc.html());
                path.forEach(page::setPath);
                page.setCode(response.statusCode());
                pageRepository.save(page);
                lemmaService.getBodyLemmas(site, doc, page);
                lemmaService.getHeadLemmas(site, doc, page);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
