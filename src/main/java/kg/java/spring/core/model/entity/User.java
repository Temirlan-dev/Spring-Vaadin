package kg.java.spring.core.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import kg.java.spring.core.model.enums.Role;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String username;
    @NotNull
    @JsonIgnore
    private String hashedPassword;
    @NotNull
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
}
