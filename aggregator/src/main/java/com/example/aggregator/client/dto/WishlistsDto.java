package com.example.aggregator.client.dto;

import java.util.ArrayList;
import java.util.List;

public class WishlistsDto {


    private List<WishlistDto> wishlists = new ArrayList<>();

    public List<WishlistDto> getWishlists() {
        return wishlists;
    }

    public void setWishlists(List<WishlistDto> wishlist) {
        this.wishlists = wishlist;
    }
}
