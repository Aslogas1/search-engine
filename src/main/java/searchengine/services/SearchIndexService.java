package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.SearchIndex;
import searchengine.repository.SearchIndexRepository;

@Service
@RequiredArgsConstructor
public class SearchIndexService {

    private final SearchIndexRepository searchIndexRepository;

    public void saveSearchIndex(SearchIndex searchIndex) {
        searchIndexRepository.save(searchIndex);
    }

}
