package searchengine.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexingResponseError implements Responsable {

    private final boolean result = false;
    private String error;

    public IndexingResponseError generateStartIndexingError() {
        IndexingResponseError indexingResponseError = new IndexingResponseError();
        indexingResponseError.setError("Индексация уже запущена");
        return indexingResponseError;
    }

    public IndexingResponseError generateStopIndexingError() {
        IndexingResponseError indexingResponseError = new IndexingResponseError();
        indexingResponseError.setError("Индексация не запущена");
        return indexingResponseError;
    }

    public IndexingResponseError generateIndexPageError() {
        IndexingResponseError indexingResponseError = new IndexingResponseError();
        indexingResponseError
                .setError("Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
        return indexingResponseError;
    }

    public IndexingResponseError generateSearchError() {
        IndexingResponseError indexingResponseError = new IndexingResponseError();
        indexingResponseError.setError("Задан пустой поисковый запрос");
        return indexingResponseError;
    }

    public IndexingResponseError generateCommonError() {
        IndexingResponseError indexingResponseError = new IndexingResponseError();
        indexingResponseError.setError("Указанная страница не найдена");
        return indexingResponseError;
    }
}
