package main;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> path = JsoupParser.getChildren("http://www.playback.ru/")
                .stream().map(s -> s.replaceAll("http://www.playback.ru", "")).toList();
        List<Integer> statusCode = JsoupParser.getStatusCode();
        List<String> content = JsoupParser.getContent();
        for (int i = 0; i < path.size(); i++) {
            UploadSqlData.uploadData(path.get(i), statusCode.get(i), content.get(i));
        }

    }
}
