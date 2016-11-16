package com.example.olg.wishlist;

import java.util.List;

public class Wishlists {

    public List<Wishlist> getWishlists() {
        return wishlists;
    }

    public void setWishlists(List<Wishlist> wishlists) {
        this.wishlists = wishlists;
    }

    private List<Wishlist> wishlists;

    public Wishlists(List<Wishlist> wishlists) {
        this.wishlists = wishlists;
    }
}
