package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;

    public Page generatePage(Site site, Document doc) {
        Page page = new Page();
        page.setSiteId(site.getId());
        page.setContent(doc.html());
        page.setPath(doc.baseUri());
        try {
            page.setCode(doc.connection().execute().statusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        pageRepository.save(page);
        return page;
    }

    public void save(Page page) {
        pageRepository.save(page);
    }

    public void deleteAndFlush() {
        pageRepository.deleteAll();
        pageRepository.flush();
    }
}
