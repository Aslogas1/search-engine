package main;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "link")
@Getter
@Setter
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "path", columnDefinition = "TEXT")
    private String path;
    private int code;
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;
}
