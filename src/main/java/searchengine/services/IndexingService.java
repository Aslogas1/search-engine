package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SearchIndexRepository;
import searchengine.repository.SiteRepository;
import searchengine.response.IndexingResponseError;
import searchengine.response.IndexingResponseOk;
import searchengine.response.Responsable;

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
            Logger.getLogger("siteConf").info(siteConf.getUrl());
            Set<String> children = jsoupParser.getChildren(siteConf.getUrl());
            Site site = siteRepository.findByUrl(siteConf.getUrl()).isPresent() ? siteRepository
                    .findByUrl(siteConf.getUrl()).get() : jsoupParser.generateAndSaveSite(siteConf);
            Logger.getLogger("site").info(site.getUrl());
            //Site site = jsoupParser.generateAndSaveSite(siteConf);
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
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if (url.matches(regex)) {
            Site site = jsoupParser.indexSite(addSlash(url), sites);
            Document doc = jsoupParser.generateConnection(url);
            Page page = jsoupParser.generatePage(Objects.requireNonNull(site), doc);
            try {
                LemmaFinder.getInstance().generateLemmas(site, doc, page, searchIndexRepository);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ResponseEntity<Responsable> startIndexingResponse() {
        Responsable response;
        int pagesCount = getPagesCount();
        startIndexing();
        if (pagesCount >= getPagesCount()) {
            response = IndexingResponseError.generateCommonError();
        } else response = new IndexingResponseOk();
        return ResponseEntity.ok(response);
    }

    public Responsable indexPageResponse(String url) {
        Responsable response;
        int pagesCount = getPagesCount();
        pageIndexing(addSlash(url));
        if (pagesCount >= getPagesCount()) {
            response = IndexingResponseError.generateCommonError();
        } else if (pageRepository.findByPath(addSlash(url)).isPresent()) {
            response = new IndexingResponseOk();
        } else if (url.isBlank()) {
            response = IndexingResponseError.generateIndexPageError();
        } else {
            response = new IndexingResponseOk();
        }
        return response;
    }

    private static String addSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    private void removingExistingSitesAndPages() {
        pageRepository.deleteAll();
        pageRepository.flush();
        siteRepository.deleteAll();
        siteRepository.flush();
    }

    public int getPagesCount() {
        return pageRepository.findAll().size();
    }

    //todo останавливать все потоки и записывать в базу данных для всех сайтов, страницы которых ещё не удалось обойти,
    // состояние FAILED и текст ошибки «Индексация остановлена пользователем».
    public void stopIndexing() {

    }
}
