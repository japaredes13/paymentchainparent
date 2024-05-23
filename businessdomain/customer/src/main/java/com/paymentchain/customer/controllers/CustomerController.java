package com.paymentchain.customer.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.repositories.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    private final WebClient.Builder webClientBuilder;

    public CustomerController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    HttpClient client = HttpClient.create()
    //Connection Timeout: is a period within which a connection between a client and a server must be established
    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
    .option(ChannelOption.SO_KEEPALIVE, true)
    .option(EpollChannelOption.TCP_KEEPIDLE, 300)
    .option(EpollChannelOption.TCP_KEEPINTVL, 60)
    //Response Timeout: The maximun time we wait to receive a response after sending a request
    .responseTimeout(Duration.ofSeconds(1))
    // Read and Write Timeout: A read timeout occurs when no data was read within a certain
    //period of time, while the write timeout when a write operation cannot finish at a specific time
    .doOnConnected(connection -> {
        connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
        connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
    });


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
        input.getProducts().forEach(x -> x.setCustomer(input));
        Customer customerSave = customerRepository.save(input);
        return ResponseEntity.ok(customerSave);
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
        Customer customer = customerRepository.findByCode(code);
        if (customer != null) {
            List<CustomerProduct> products = customer.getProducts();

            //for each product find it name
            products.forEach(product -> {
                String productName = getProductName(product.getProductId());
                product.setProductName(productName);
            });

        }
        return customer;
    }


    /**
     * Call Product Microservice, find a product by id and return it name
     *
     * @param id of product to find
     * @return name of product if it was found
     */
    private String getProductName(Long id) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8083/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8083/product"))
                .build();
        JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();
        String name = block.get("name").asText();
        return name;
    }


}
