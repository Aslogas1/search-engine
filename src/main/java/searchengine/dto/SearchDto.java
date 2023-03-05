package searchengine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDto {

    private String site;
    private String siteName;
    private String url;
    private String title;
    private String snippet;
    private Double relevance;
}
