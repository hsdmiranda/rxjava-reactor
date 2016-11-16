package com.example.olg;

import com.example.olg.reviews.Product;
import com.example.olg.reviews.ProductContent;
import com.example.olg.reviews.Products;
import com.example.olg.wishlist.Wishlist;
import com.example.olg.wishlist.Wishlists;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class OlgController {

    @GetMapping("/wishlist")
    public Wishlists getWishlists(@RequestParam(value = "author_id") String authorId) {
        System.out.println("wishlist " + authorId);
        List<Wishlist> wishlists = new ArrayList<>();

        Wishlist wishlist = new Wishlist();
        wishlist.setId("1");
        wishlist.setName("Wishlist1");
        wishlist.setProductsId(Arrays.asList("1","2","3"));
        wishlists.add(wishlist);

        wishlist = new Wishlist();
        wishlist.setId("2");
        wishlist.setName("Wishlist2");
        wishlist.setProductsId(Arrays.asList("2","4"));
        wishlists.add(wishlist);


        return new Wishlists(wishlists);
    }

    @GetMapping("/ratings")
    public Products getProductRatings(@RequestParam(value = "product_id") String productId) throws InterruptedException {
        System.out.println("ratings" + productId);

        Products products = new Products();

        products.addProduct(new Product(productId, 5f));
        products.addProduct(new Product(productId, 3f));
        products.addProduct(new Product(productId, 4f));
        products.addProduct(new Product(productId, 1f));

        Thread.sleep(50);
        return products;
    }

    @GetMapping("/products")
    public ProductContent getProductContent(@RequestParam(value = "product_id") String productId) throws InterruptedException {
        System.out.println("product content" + productId);
        Thread.sleep(50);
        return new ProductContent(productId,"Product " + productId);
    }
}
