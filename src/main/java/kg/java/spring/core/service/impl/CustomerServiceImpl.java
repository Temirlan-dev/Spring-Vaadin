package kg.java.spring.core.service.impl;

import kg.java.spring.core.model.ResponseDB;
import kg.java.spring.core.model.entity.Customer;
import kg.java.spring.core.model.enums.ResultDB;
import kg.java.spring.core.repository.CustomerRepository;
import kg.java.spring.core.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    @Override
    public ResponseDB save(Customer customer) {
        try {
            customerRepository.save(Customer.builder()
                    .name(customer.getName())
                    .lastname(customer.getLastname())
                    .card(customer.getCard())
                    .startDate(customer.getStartDate())
                    .endDate(customer.getEndDate())
                    .build());
            return new ResponseDB(ResultDB.SUCCESS, "Успешно");
        } catch (Exception e) {
            return new ResponseDB(ResultDB.ERROR, e.getMessage());
        }
    }

    @Override
    public List<Customer> getCustomer() {
        return customerRepository.findAll();
    }
}
