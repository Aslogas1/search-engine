package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.nodes.Document;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SearchIndex;
import searchengine.model.Site;
import searchengine.repository.SearchIndexRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LemmaFinder {

    private final LuceneMorphology luceneMorphology;
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    public static LemmaFinder getInstance() throws IOException {
        LuceneMorphology morphology = new RussianLuceneMorphology();
        return new LemmaFinder(morphology);
    }

    public LemmaFinder(LuceneMorphology luceneMorphology) {
        this.luceneMorphology = luceneMorphology;
    }

    public Map<String, Integer> collectLemmas(String text) {
        String[] words = arrayContainsRussianWords(text);
        HashMap<String, Integer> lemmas = new HashMap<>();

        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }

            List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
            if (anyWordBaseBelongToParticle(wordBaseForms)) {
                continue;
            }

            List<String> normalForms = luceneMorphology.getNormalForms(word);
            if (normalForms.isEmpty()) {
                continue;
            }

            String normalWord = normalForms.get(0);

            if (lemmas.containsKey(normalWord)) {
                lemmas.put(normalWord, lemmas.get(normalWord) + 1);
            } else {
                lemmas.put(normalWord, 1);
            }
        }

        return lemmas;
    }

    private boolean anyWordBaseBelongToParticle(List<String> wordBaseForms) {
        return wordBaseForms.stream().anyMatch(this::hasParticleProperty);
    }

    private boolean hasParticleProperty(String wordBase) {
        for (String property : particlesNames) {
            if (wordBase.toUpperCase().contains(property)) {
                return true;
            }
        }
        return false;
    }

    private String[] arrayContainsRussianWords(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("([^а-я\\s])", " ")
                .trim()
                .split("\\s+");
    }

    public void generateLemmas(Site site, Document doc, Page page, SearchIndexRepository searchIndexRepository) {
        try {
            for (Map.Entry<String, Integer> entry : LemmaFinder.getInstance()
                    .collectLemmas(doc.head().getAllElements().text()).entrySet()) {
                Lemma lemma = new Lemma(site.getId(), entry.getKey(), entry.getValue());
                searchIndexRepository.save(new SearchIndex(page, lemma, lemma.getFrequency().floatValue()));
            }
            for (Map.Entry<String, Integer> entry : LemmaFinder.getInstance()
                    .collectLemmas(doc.body().getAllElements().text()).entrySet()) {
                Lemma lemma = new Lemma(site.getId(), entry.getKey(), entry.getValue());
                searchIndexRepository.save(new SearchIndex(page, lemma, lemma.getFrequency().floatValue() * 0.8f));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}