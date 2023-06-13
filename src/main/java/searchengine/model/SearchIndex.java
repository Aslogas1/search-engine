package searchengine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "search_index")
@RequiredArgsConstructor
public class SearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "page_id", referencedColumnName = "id")
    private Page page;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lemma_id", referencedColumnName = "id")
    private Lemma lemma;
    @Column(name = "lemma_rank", nullable = false)
    private Float rank;

}
