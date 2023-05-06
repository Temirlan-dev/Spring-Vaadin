package kg.java.spring.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "season_card")
public class SeasonCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Float sum;
    @OneToMany (mappedBy="card")
    private List<Customer> customerCollection;
}
