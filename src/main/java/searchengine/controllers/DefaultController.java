package searchengine.controllers;

import org.springframework.web.bind.annotation.RequestMapping;

public interface DefaultController {

    @RequestMapping("/")
    String index();
}
