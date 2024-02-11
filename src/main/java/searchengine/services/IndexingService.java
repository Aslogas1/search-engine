package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SearchIndexRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
//TODO Обход каждого из сайтов, перечисленных в конфигурационном файле, должен запускаться в отдельном потоке.
// Поскольку на страницах сайта могут находиться повторяющиеся ссылки, сервис должен проверять запросом к базе данных,
// заходил ли он по каждой очередной ссылке или нет.

@Service
@RequiredArgsConstructor
public class IndexingService {

    private final PageRepository pageRepository;
    private final JsoupParser jsoupParser;
    private final SearchIndexRepository searchIndexRepository;
    private final SiteRepository siteRepository;
    private final SitesList sites;


    public void startIndexing() {
        removingExistingSitesAndPages();
        for (searchengine.config.Site siteConf : sites.getSites()) {
            Document doc;
            Set<String> children = jsoupParser.getChildren(siteConf.getUrl());
            Site site = jsoupParser.generateAndSaveSite(siteConf);
            jsoupParser.addFirstPage(site, siteConf.getUrl());
            for (String child : children) {
                //TODO здесь вставлять ForkJoin
                doc = jsoupParser.generateConnection(child);
                site.setStatusTime(new Date().getTime());
                Page page = jsoupParser.generatePage(site, doc);
                try {
                    LemmaFinder.getInstance().generateLemmas(site, doc, page, searchIndexRepository);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            jsoupParser.changeSiteStatus(site);
        }
    }

    public void pageIndexing(String url) {
        Site site = jsoupParser.indexSite(url, sites);
        Logger.getLogger("pageIndexLogger").info("siteUrl: " + site.getUrl());
        Document doc = jsoupParser.generateConnection(url);
        try {
            Logger.getLogger("pageIndexingLogger").info("statusCode: " + doc.connection().execute().statusCode());
            LemmaFinder.getInstance().generateLemmas(site, doc, jsoupParser
                    .generatePage(Objects.requireNonNull(site), doc), searchIndexRepository);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jsoupParser.changeSiteStatus(site);
    }

    private void removingExistingSitesAndPages() {
        pageRepository.deleteAll();
        pageRepository.flush();
        siteRepository.deleteAll();
        siteRepository.flush();
    }

    //todo останавливать все потоки и записывать в базу данных для всех сайтов, страницы которых ещё не удалось обойти,
    // состояние FAILED и текст ошибки «Индексация остановлена пользователем».
    public void stopIndexing() {

    }
}
