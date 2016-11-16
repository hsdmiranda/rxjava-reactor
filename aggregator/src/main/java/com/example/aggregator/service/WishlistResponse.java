package com.example.aggregator.service;

import java.util.List;

public class WishlistResponse {

    private String id;

    private String name;

    public List<WishlistItemResponse> getWishlistItems() {
        return wishlistItems;
    }

    public void setWishlistItems(List<WishlistItemResponse> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }

    private List<WishlistItemResponse> wishlistItems;

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
