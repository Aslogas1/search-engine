package main;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        UploadSqlData.uploadData();
    }

    public static List<Page> getAllPages() throws IOException {
        List<String> path = JsoupParser.getChildren("http://www.playback.ru/")
                .stream().map(s -> s.replaceAll("http://www.playback.ru", "")).toList();
        List<Integer> statusCode = JsoupParser.getStatusCode();
        List<String> content = JsoupParser.getContent();
        List<Page> pageList = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            Page page = new Page();
            pageList.add(page);
            pageList.get(i).setCode(statusCode.get(i));
            pageList.get(i).setPath(path.get(i));
            pageList.get(i).setContent(content.get(i));
        }
        return pageList;
    }

    public static Map<String, Integer> getAllLemmas() throws IOException {
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorphology);
        List<String> lemmasInfo = JsoupParser.getLemmasInfo();
        StringBuilder builder = new StringBuilder();
        for (String s : lemmasInfo) {
            builder.append(s).append(" ");
        }
        return lemmaFinder.collectLemmas(builder.toString());
    }
}
