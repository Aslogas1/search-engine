package searchengine.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexingResponseOk implements Responsable {

    private final boolean result = true;
}
