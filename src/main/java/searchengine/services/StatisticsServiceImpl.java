package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SitesList sites;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(true);
        int pages = 0;
        int lemmas = 0;
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = sites.getSites();
        for (Site site : sitesList) {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            searchengine.model.Site byUrl = siteRepository.findByUrl(site.getUrl());
            if (byUrl != null) {
                item.setName(Objects.requireNonNull(byUrl).getName());
                item.setUrl(byUrl.getUrl());
                pages = pageRepository.findAll().size();
                lemmas = lemmaRepository.findAll().size();
                item.setPages(pages);
                item.setLemmas(lemmas);
                item.setStatus(byUrl.getStatus().name());
                item.setError(getError(byUrl)
                );
                item.setStatusTime(System.currentTimeMillis());
            } else {
                item.setName(site.getName());
                item.setUrl(site.getUrl());
                item.setLemmas(0);
                item.setPages(0);
                item.setStatus("Unknown");
                item.setError("Unknown");
                item.setStatusTime(0);
            }

            total.setPages(total.getPages() + pages);
            total.setLemmas(total.getLemmas() + lemmas);
            detailed.add(item);
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }

    private static String getError(searchengine.model.Site byUrl) {
        String error;
        if (byUrl.getLastError() != null) {
            error = byUrl.getLastError();
        } else error = "No errors";
        return error;
    }
}
