package kg.java.spring.core.repository;

import kg.java.spring.core.model.entity.SeasonCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SeasonCardRepository extends JpaRepository<SeasonCard, Integer> {
}
