package com.paymentchain.product.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Product {
   
   @GeneratedValue(strategy = GenerationType.AUTO)  
   @Id
   private Long id;
   private String name;
   private String code;
   
   
}
