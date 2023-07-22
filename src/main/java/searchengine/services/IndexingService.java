package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Page;
import searchengine.model.Site;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IndexingService {

    private final PageService pageService;
    private final JsoupParser jsoupParser;
    private final LemmaService lemmaService;
    private final SiteService siteService;
    private final SitesList sites;


    public void pageIndexing(String url) {
        Site site = siteService.saveSite(url, url.substring(12));
        Document doc = jsoupParser.generateConnection(url);
        Page page = null;
        try {
            page = new Page(site.getId(), url, doc.connection().execute().statusCode(), doc.html());
        } catch (IOException e) {
            e.printStackTrace();
        }
        pageService.save(page);
        lemmaService.generateLemmas(site, doc, page);
        siteService.changeSiteStatus(site);

    }


    public void startIndexing() {
        removeExistingSitesAndPages();
        for (searchengine.config.Site siteConf : sites.getSites()) {
            Set<String> children = jsoupParser.getChildren(siteConf.getUrl());
            Site site = siteService.saveSite(siteConf.getUrl(), siteConf.getName());
            for (String child : children) {
                if (child.startsWith("http") && !child.contains("https://twitter.com/") && !child.endsWith(".pdf")) {
                    Document doc = jsoupParser.generateConnection(child);
                    Page page = pageService.generatePage(site, doc);
                    lemmaService.generateLemmas(site, doc, page);
                }
            }
            siteService.changeSiteStatus(site);
        }
    }

    private void removeExistingSitesAndPages() {
        pageService.deleteAndFlush();
        siteService.deleteAndFlush();
    }


    public void stopIndexing() {

    }
}
