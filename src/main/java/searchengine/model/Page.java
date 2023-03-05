package searchengine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "page")
@Getter
@Setter
@RequiredArgsConstructor
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

    //По полю path должен быть установлен индекс, чтобы поиск по нему был быстрым,
    // когда в нём будет много ссылок. Индексы рассмотрены в курсе «Язык запросов SQL».
}
