package com.example.aggregator;

import com.example.aggregator.service.WishlistService;
import com.netflix.config.ConfigurationManager;

import java.io.IOException;

public class AggregatorApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		ConfigurationManager.loadPropertiesFromResources("application.properties");

        boolean execute = true;
        WishlistService wishlistService = new WishlistService();

        while (execute) {
            wishlistService.getWishlistsRxJava("1") // This is the call using RXJAVA. No JSON Deserialization error occurs when using this.
            //wishlistService.getWishlistsReactor("1")  // Reactor call. You will see that JSON deserialization happens from time to time. Due to response going to wrong methods.
                .subscribe(
                        wishlistResponse -> System.out.println(wishlistResponse),
                        t-> {
                            System.out.println(t);
                            System.exit(0);
                        });
            Thread.sleep(40);
        }
    }
}
