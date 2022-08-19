package main;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "field")
@Getter
@Setter
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "selector", nullable = false)
    private String selector;
    @Column(name = "weight", nullable = false)
    private float weight;

    public float getWeight() {
        weight = 0;
        switch (getName()) {
            case "title" -> weight = 1.0f;
            case "body" -> weight = 0.8f;
        }
        return weight;
    }
}
