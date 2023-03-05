package searchengine.controllers;

import org.springframework.stereotype.Controller;

@Controller
public class DefaultControllerImpl implements DefaultController {

    @Override
    public String index() {
        return "index";
    }
}
