package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.response.IndexingResponseOk;
import searchengine.response.Responsable;
import searchengine.services.IndexingService;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiControllerImpl implements ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;

    @Override
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @Override
    public ResponseEntity<Responsable> startIndexing() {
        indexingService.startIndexing();
        IndexingResponseOk response = new IndexingResponseOk();
        response.setResult(true);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Responsable> stopIndexing() {
        IndexingResponseOk response = new IndexingResponseOk();
        response.setResult(true);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Responsable> indexPage(String url) {
        IndexingResponseOk response = new IndexingResponseOk();
        response.setResult(response.isResult());
        indexingService.pageIndexing(url);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Responsable> search(String query, String site, int offset, int limit) {
        return null;
    }
}
