package searchengine.repository;

import org.jsoup.nodes.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;
import searchengine.model.Site;

import java.io.IOException;
import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    void findBySiteId(Integer id);

    default Page generatePage(Site site, List<String> path, Document doc) throws IOException {
        Page page = new Page();
        page.setSiteId(site.getId());
        page.setContent(doc.html());
        path.forEach(page::setPath);
        page.setCode(doc.connection().execute().statusCode());
        save(page);
        return page;
    }
}
