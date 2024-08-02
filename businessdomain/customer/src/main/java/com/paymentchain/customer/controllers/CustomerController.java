package com.paymentchain.customer.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.client.TransactionClient;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.BussinesRuleException;
import com.paymentchain.customer.repositories.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private Environment env;

    @Autowired
    private TransactionClient transactionClient;

    @GetMapping("/check")
    public String check(){
        return "The value propertie is: ".concat(
                env.getProperty("custom.activeprofileName"));
    }

    @GetMapping("")
    public ResponseEntity<?> findAll(){
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id){
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent())
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody Customer input) throws BussinesRuleException, UnknownHostException {
        Customer customer = transactionClient.createCustomer(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable("id") Long id, @RequestBody Customer input){
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent()){
            Customer customerUpdate = customer.get();
            customerUpdate.setCode(input.getCode());
            customerUpdate.setName(input.getName());
            customerUpdate.setPhone(input.getPhone());

            customerUpdate.setIban(input.getIban());
            customerUpdate.setSurname(input.getSurname());
            customerUpdate.setProducts(input.getProducts());
            Customer custoemrSave = customerRepository.save(customerUpdate);
            return new ResponseEntity<>(custoemrSave, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        customerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/full")
    public Customer getByCode(@RequestParam(name = "code") String code) {
        Customer customer = transactionClient.getCustomerByCode(code);
        return customer;
    }

}
