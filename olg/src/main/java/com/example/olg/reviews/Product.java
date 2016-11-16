package com.example.olg.reviews;

public class Product {

    private String id;

    private Float averageRating;

    public Product(String id, Float averageRating) {
        this.id = id;
        this.averageRating = averageRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }
}
