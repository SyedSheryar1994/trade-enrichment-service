package com.verygoodbank.tes.web.model;

import com.verygoodbank.tes.web.service.TradeEnrichmentService;

import java.math.BigDecimal;

public class Trade {
    private int productId;
    private String date;
    private String currency;
    private BigDecimal price;
    private String productName;

    public Trade(int productId, String date, String currency, BigDecimal price, String productName){
        this.productId = productId;
        this.date = date;
        this.currency = currency;
        this.price = price;
        this.productName = productName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
