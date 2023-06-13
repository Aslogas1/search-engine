package searchengine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "lemma")
@RequiredArgsConstructor
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "site_id", nullable = false)
    private Integer siteId;
    @Column(nullable = false)
    private String lemma;
    @Column(nullable = false)
    private Integer frequency;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lemma")
    private List<SearchIndex> indexList;

}
