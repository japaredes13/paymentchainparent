package com.paymentchain.customer.controllers;

import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;


    @GetMapping("/")
    public List<Customer> findAll(){
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id){
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent())
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody Customer input){
        Customer customerSave = customerRepository.save(input);
        return ResponseEntity.ok(customerSave);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer input){
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent()){
            Customer customerUpdate = customer.get();
            customerUpdate.setName(input.getName());
            customerUpdate.setPhone(input.getPhone());

            customerUpdate.setIban(input.getIban());
            customerUpdate.setSurname(input.getSurname());
            Customer custoemrSave = customerRepository.save(customerUpdate);
            return new ResponseEntity<>(custoemrSave, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        customerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
