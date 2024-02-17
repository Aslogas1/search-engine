package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.response.Responsable;


public interface ApiController {

    @GetMapping("/statistics")
    ResponseEntity<StatisticsResponse> statistics();

    @GetMapping("/startIndexing")
    ResponseEntity<Responsable> startIndexing() ;

    @GetMapping("/stopIndexing")
    ResponseEntity<Responsable> stopIndexing();

    @PostMapping("/indexPage")
    ResponseEntity<Responsable> indexPage(String url);

    @GetMapping("/search")
    ResponseEntity<Responsable> search(String query, String site,
                                       @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
                                       @RequestParam(name = "limit", defaultValue = "20", required = false) int limit);

}
