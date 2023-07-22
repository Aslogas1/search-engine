package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SearchIndex;
import searchengine.model.Site;
import searchengine.repository.LemmaRepository;
import searchengine.repository.SearchIndexRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LemmaService {

    private final LemmaRepository lemmaRepository;
    private final SearchIndexRepository searchIndexRepository;

    public void getHeadLemmas(Site site, Document doc, Page page) {
        try {
            Map<String, Integer> headLemmas = Objects.requireNonNull(LemmaFinder.getInstance())
                    .collectLemmas(doc.head().getAllElements().text());
            for (Map.Entry<String, Integer> entry : headLemmas.entrySet()) {
                Lemma lemma = new Lemma(site.getId(), entry.getKey(), entry.getValue());
                lemmaRepository.save(lemma);
                searchIndexRepository.save(new SearchIndex(page, lemma, lemma.getFrequency().floatValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBodyLemmas(Site site, Document doc, Page page) {
        try {
            Map<String, Integer> bodyLemmas = Objects.requireNonNull(LemmaFinder.getInstance())
                    .collectLemmas(doc.body().getAllElements().text());
            for (Map.Entry<String, Integer> entry : bodyLemmas.entrySet()) {
                Lemma lemma = new Lemma(site.getId(), entry.getKey(), entry.getValue());
                lemmaRepository.save(lemma);
                searchIndexRepository.save(new SearchIndex(page, lemma, lemma.getFrequency().floatValue() * 0.8f));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void generateLemmas(Site site, Document doc, Page page) {
        getBodyLemmas(site, doc, page);
        getHeadLemmas(site, doc, page);
    }
}
