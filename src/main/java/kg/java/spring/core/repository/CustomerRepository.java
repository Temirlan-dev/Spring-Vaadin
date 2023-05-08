package kg.java.spring.core.repository;

import kg.java.spring.core.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("select c from Customer c " +
            "where lower(c.name) like lower(concat('%', :name, '%'))")
    List<Customer> findByName(String name);
}
