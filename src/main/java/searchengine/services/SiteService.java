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

    public Site saveSite(String url) {
        Site site = new Site();
        site.setStatus(Status.INDEXING);
        site.setUrl(url);
        site.setName("PlayBack.Ru");
        site.setStatusTime(new Date().getTime());
        site.setLastError("Done");
        return siteRepository.save(site);
    }
}
