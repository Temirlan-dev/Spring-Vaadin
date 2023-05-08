package kg.java.spring.core.service;

import kg.java.spring.core.model.ResponseDB;
import kg.java.spring.core.model.entity.Customer;
import java.util.List;

public interface CustomerService {
    ResponseDB save(Customer customer);
    List<Customer> getCustomer();
    void delete(Customer customerId);
    List<Customer> findAllCustomer(String filterByName);
}
