package kg.java.spring.core.service;

import kg.java.spring.core.model.ResponseDB;
import kg.java.spring.core.model.entity.Customer;

public interface CustomerService {
    ResponseDB save(Customer customer);
}
