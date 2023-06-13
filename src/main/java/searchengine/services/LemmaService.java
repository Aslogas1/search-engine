package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SearchIndex;
import searchengine.model.Site;
import searchengine.repository.LemmaRepository;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LemmaService {

    private final LemmaRepository lemmaRepository;
    private final SearchIndexService searchIndexService;

    public void saveLemma(Lemma lemma) {
        lemmaRepository.save(lemma);
    }

    public void getHeadLemmas(Site site, Document doc, Page page) throws IOException {
        LemmaFinder finder = LemmaFinder.getInstance();
        Map<String, Integer> headLemmas = finder.collectLemmas(doc.head().getAllElements().text());
        for (Map.Entry<String, Integer> entry : headLemmas.entrySet()) {
            SearchIndex searchIndex = new SearchIndex();
            Lemma lemma = new Lemma();
            Integer value = entry.getValue();
            String key = entry.getKey();
            lemma.setLemma(key);
            lemma.setFrequency(value);
            lemma.setSiteId(site.getId());
            saveLemma(lemma);
            searchIndex.setPage(page);
            searchIndex.setLemma(lemma);
            searchIndex.setRank(lemma.getFrequency().floatValue());
            searchIndexService.saveSearchIndex(searchIndex);
        }
    }

    public void getBodyLemmas(Site site, Document doc, Page page) throws IOException {
        LemmaFinder finder = LemmaFinder.getInstance();
        Map<String, Integer> bodyLemmas = finder.collectLemmas(doc.body().getAllElements().text());
        for (Map.Entry<String, Integer> entry : bodyLemmas.entrySet()) {
            SearchIndex searchIndex = new SearchIndex();
            Lemma lemma = new Lemma();
            Integer value = entry.getValue();
            String key = entry.getKey();
            lemma.setLemma(key);
            lemma.setFrequency(value);
            lemma.setSiteId(site.getId());
            saveLemma(lemma);
            searchIndex.setPage(page);
            searchIndex.setLemma(lemma);
            searchIndex.setRank(lemma.getFrequency() * 0.8f);
            searchIndexService.saveSearchIndex(searchIndex);
        }
    }
}
