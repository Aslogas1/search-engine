package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.dto.Status;
import searchengine.model.Site;

import java.util.Date;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
    Site findByUrl(String url);

    default Site saveSite(String url) {
        Site site = new Site(Status.INDEXING, new Date().getTime(), url, "PlayBack.Ru");
        return save(site);
    }

    default void changeSiteStatus(Site site) {
        site.setStatus(Status.INDEXED);
        save(site);
        if (findAll().size() == 0) {
            site.setStatus(Status.FAILED);
            site.setLastError("Индексация не выполнена");
        }
    }
}
