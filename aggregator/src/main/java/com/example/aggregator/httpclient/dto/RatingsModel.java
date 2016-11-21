package com.example.aggregator.httpclient.dto;

import java.util.List;

public class RatingsModel {
    private List<ProductRatingDto> products;

    public List<ProductRatingDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRatingDto> productDto) {
        this.products = productDto;
    }
}
