package com.paymentchain.customer.entities;


import jakarta.persistence.*;
import lombok.Data;
import java.util.List;



@Entity
@Data
public class Customer {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    private String code;
    private String name;
    private String phone;

    private String iban;
    private String surname;
    private String address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerProduct> products;

    @Transient
    private List<?> transactions;

}
