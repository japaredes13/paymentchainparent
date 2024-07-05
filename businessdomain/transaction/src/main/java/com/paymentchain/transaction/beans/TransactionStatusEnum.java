package com.paymentchain.transaction.beans;

import org.apache.logging.log4j.util.Strings;

public enum TransactionStatusEnum {

    PENDIENTE("pendiente", "PENDIENTE"),
    LIQUIDADA("liquidada", "LIQUIDADA"),
    RECHAZADA("rechazada", "RECHAZADA"),
    CANCELADA("cancelada", "CANCELADA"),
    ;

    private String code;
    private String description;

    TransactionStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static TransactionStatusEnum fromValue(String valor) {
        if(Strings.isBlank(valor)) return null;
        for(TransactionStatusEnum codTransactionStatusEnum : TransactionStatusEnum.values()){
            if(codTransactionStatusEnum.code.equalsIgnoreCase(valor)) {
                return codTransactionStatusEnum;
            }
        }
        return null;
    }

}
