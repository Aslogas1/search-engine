package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.dto.Status;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SearchIndex;
import searchengine.model.Site;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SearchIndexRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IndexingService {

    private final PageRepository pageRepository;
    private final JsoupParser jsoupParser;
    private final LemmaRepository lemmaRepository;
    private final SearchIndexRepository searchIndexRepository;
    private final SiteRepository siteRepository;


    public Site saveSite(String url) {
        Site site = new Site();
        site.setStatus(Status.INDEXING);
        site.setUrl(url);
        site.setName("PlayBack.Ru");
        site.setStatusTime(new Date().getTime());
        site.setLastError("Done");
        return siteRepository.save(site);
    }

    public void getHeadLemmas(Site site, Document doc, Page page) throws IOException {
        LemmaFinder finder = LemmaFinder.getInstance();
        Map<String, Integer> headLemmas = finder.collectLemmas(doc.head().getAllElements().text());
        for (Map.Entry<String, Integer> entry : headLemmas.entrySet()) {
            Lemma lemma = new Lemma(site.getId(), entry.getKey(), entry.getValue());
            lemmaRepository.save(lemma);
            searchIndexRepository.save(new SearchIndex(page, lemma, lemma.getFrequency().floatValue()));
        }
    }

    public void getBodyLemmas(Site site, Document doc, Page page) throws IOException {
        LemmaFinder finder = LemmaFinder.getInstance();
        Map<String, Integer> bodyLemmas = finder.collectLemmas(doc.body().getAllElements().text());
        for (Map.Entry<String, Integer> entry : bodyLemmas.entrySet()) {
            Lemma lemma = new Lemma(site.getId(), entry.getKey(), entry.getValue());
            lemmaRepository.save(lemma);
            searchIndexRepository.save(new SearchIndex(page, lemma, lemma.getFrequency().floatValue() * 0.8f));
        }
    }

    public void indexing(String url) throws IOException {
        Set<String> children = jsoupParser.getChildren(url);
        Site site = saveSite(url);
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
            getBodyLemmas(site, doc, page);
            getHeadLemmas(site, doc, page);
        }
    }
}
