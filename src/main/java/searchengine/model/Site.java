package searchengine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import searchengine.dto.Status;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;
    @Column(nullable = false)
    private Status status;
    @Column(name = "status_time", nullable = false)
    private Long statusTime;
    @Column(name = "last_error")
    private String lastError;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String name;
}
