package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "page")
@Getter
@Setter
@NoArgsConstructor
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "site_id", nullable = false)
    private Integer siteId;
    @Column(nullable = false)
    private String path;
    @Column(nullable = false)
    private Integer code;
    @Column(nullable = false)
    private String content;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "page")
    private List<SearchIndex> indexList;

    public Page(Integer siteId, String path, Integer code, String content) {
        this.siteId = siteId;
        this.path = path;
        this.code = code;
        this.content = content;
    }

//По полю path должен быть установлен индекс, чтобы поиск по нему был быстрым,
    // когда в нём будет много ссылок. Индексы рассмотрены в курсе «Язык запросов SQL».
}
