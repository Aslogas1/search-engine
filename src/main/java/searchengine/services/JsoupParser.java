package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.dto.Status;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yaml")
public class JsoupParser extends RecursiveTask<Integer> {
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    @Value("${user.agent}")
    private String userAgent;

    //todo расписать этот метод в compute
    public Set<String> getChildren(String link) {
        Document doc;
        try {
            doc = Jsoup.connect(link).ignoreHttpErrors(true).maxBodySize(0).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc.select("a").stream()
                .parallel()
                .map(el -> el.attr("abs:href"))
                .filter(el -> !el.contains("#") && !el.contains("+"))
                .collect(Collectors.toSet());
    }

    //todo Для перехода по очередной ссылке должен создаваться новый поток при помощи Fork-Join. Этот поток будет получать
    // содержимое страницы и перечень ссылок, которые есть на этой странице (значений атрибутов href HTML-тегов <a>), при
    // помощи JSOUP.
    public Document generateConnection(String child) {
        Document doc = null;
        try {
            Logger.getLogger("logger").info("url: " + child);
            if (!child.contains("@")) {
                doc = Jsoup.connect(checkFormat(child))
                        .userAgent(userAgent)
                        .get();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    public Page generatePage(Site site, Document doc) {
        Page page;
        try {
            String path = doc.baseUri().replaceAll("^https?://[^/]+", "");
            if (path.isBlank()) {
                path = "/";
            }
            page = new Page(site.getId(), path,
                    doc.connection().execute().statusCode(), doc.html());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pageRepository.save(page);
    }

    public String checkFormat(String url) {
        String result;
        if (!url.startsWith("https://") && !url.contains("@")) {
            result = "https://" + url;
        } else result = url;
        return result;
    }

    public Site generateSite(searchengine.config.Site site) {
        return new Site(Status.INDEXING, new Date().getTime(), site.getUrl(), site.getName());
    }

    public Site generateAndSaveSite(searchengine.config.Site site) {
        return siteRepository.save(new Site(Status.INDEXING, new Date().getTime(), site.getUrl(), site.getName()));
    }

    //todo если произошла ошибка и обход завершить не удалось, изменять статус на FAILED и вносить в поле last_error
    // понятную информацию о произошедшей ошибке.
    public void changeSiteStatus(Site site) {
        site.setStatus(Status.INDEXED);
        siteRepository.save(site);
    }

    public Site indexSite(String url, SitesList sites) {
        Site site = null;
        for (searchengine.config.Site siteConf : sites.getSites()) {
            if (url.equals(siteConf.getUrl())) {
                site = generateSite(siteConf);
            } else site = new Site(Status.INDEXING, new Date().getTime(), url, "Unknown site");
        }
        return siteRepository.save(Objects.requireNonNull(site));
    }

    @Override
    protected Integer compute() {
        return null;
    }

    public void addFirstPage(Site site, String url) {
        Document document = generateConnection(url);
        try {
            pageRepository.save(new Page(site.getId(), site.getUrl()
                    .replaceAll("^https?://[^/]+", "/"), document.connection()
                    .execute().statusCode(), document.html()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}