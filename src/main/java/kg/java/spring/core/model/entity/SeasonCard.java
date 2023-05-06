package kg.java.spring.core.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String name;
    @NotNull
    private Float sum;
    @OneToMany (mappedBy="card")
    private List<Customer> customerCollection;
}
