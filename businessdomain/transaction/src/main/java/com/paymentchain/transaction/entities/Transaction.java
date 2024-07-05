package com.paymentchain.transaction.entities;

import com.paymentchain.transaction.beans.TransactionStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   private long id;
   private String reference;
   private String ibanAccount;
   private LocalDateTime date;
   private double amount ;
   private double fee;
   private String description;
   private TransactionStatusEnum status;
   private String channel;


}
