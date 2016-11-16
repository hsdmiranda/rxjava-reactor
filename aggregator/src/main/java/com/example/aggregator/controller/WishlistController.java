package com.example.aggregator.controller;

import com.example.aggregator.service.WishlistResponse;
import com.example.aggregator.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/wishlist")
    public DeferredResult<WishlistResponse> getWishlsit() {
        DeferredResult deferredResult = new DeferredResult();

        wishlistService.getWishlists("1")
                .subscribe(wishlistResponse -> deferredResult.setResult(wishlistResponse));

        return deferredResult;
    }

    @GetMapping("/wishlistReactor")
    public DeferredResult<WishlistResponse> getWishlsitReactor() {
        DeferredResult deferredResult = new DeferredResult();

        wishlistService.getWishlistsReactor("1")
                .subscribe(wishlistResponse -> deferredResult.setResult(wishlistResponse));

        return deferredResult;
    }
}
