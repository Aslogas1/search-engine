package searchengine.response;

import lombok.Getter;
import lombok.Setter;
import searchengine.dto.SearchDto;

@Getter
@Setter
public class searchResponse implements Responsable {

    private boolean result;
    private int count;
    private SearchDto searchDto;
}
