package searchengine.dto;

import lombok.Getter;

@Getter
public enum Status {
    INDEXING, INDEXED, FAILED;
}
