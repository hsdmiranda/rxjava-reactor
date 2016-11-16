package com.example.aggregator.client.dto;

import java.util.ArrayList;
import java.util.List;

public class WishlistDto {


    private String id;
    private String name;
    private List<String> productsId = new ArrayList<>();

    public List<String> getProductsId() {
        return productsId;
    }

    public void setProductsId(List<String> productsId) {
        this.productsId = productsId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
