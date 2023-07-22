package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.Status;
import searchengine.model.Site;
import searchengine.repository.SiteRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    public Site saveSite(String url, String name) {
        Site site = new Site(Status.INDEXING, new Date().getTime(), url, name);
        return siteRepository.save(site);
    }

    public void changeSiteStatus(Site site) {
        site.setStatus(Status.INDEXED);
        siteRepository.save(site);
        if (siteRepository.findAll().size() == 0) {
            site.setStatus(Status.FAILED);
            site.setLastError("Индексация не выполнена");
        } else site.setStatus(Status.INDEXED);
    }

    public void deleteAndFlush() {
        siteRepository.deleteAll();
        siteRepository.flush();
    }
}
