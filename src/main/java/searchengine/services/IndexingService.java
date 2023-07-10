package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SearchIndex;
import searchengine.model.Site;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SearchIndexRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
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
    private final SitesList sites;

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

    public void pageIndexing(String url) {
        Site site = siteRepository.saveSite(url, url.substring(12));
        Document doc = jsoupParser.generateConnection(url);
        try {
            Page page = new Page(site.getId(), url, doc.connection().execute().statusCode(), doc.html());
            pageRepository.save(page);
            getBodyLemmas(site, doc, page);
            getHeadLemmas(site, doc, page);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        siteRepository.changeSiteStatus(site);

    }

    public void startIndexing() {
        removingExistingSitesAndPages();
        for (searchengine.config.Site siteConf : sites.getSites()) {
            Set<String> children = jsoupParser.getChildren(siteConf.getUrl());
            Site site = siteRepository.saveSite(siteConf.getUrl(), siteConf.getName());
            List<String> path = children.stream()
                    .map(s -> s.replaceAll(siteConf.getUrl(), "")).toList();
            for (String child : children) {
                Document doc = jsoupParser.generateConnection(child);
                try {
                    Page page = pageRepository.generatePage(site, path, doc);
                    getBodyLemmas(site, doc, page);
                    getHeadLemmas(site, doc, page);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            siteRepository.changeSiteStatus(site);
        }
    }

    private void removingExistingSitesAndPages() {
        pageRepository.deleteAll();
        pageRepository.flush();
        siteRepository.deleteAll();
        siteRepository.flush();
    }

    public void stopIndexing() {

    }
}
