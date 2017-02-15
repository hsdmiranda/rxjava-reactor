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
            wishlistService.getWishlistsReactorNetty("1") // This is the call using Reactor 3 and reactor-netty.
//            wishlistService.getWishlistsRxJava("1") // This is the call using RxNetty. You can easily see the output
//            wishlistService.getWishlistsReactorNettyAndHystrix("1")  // Reactor-netty call with RxJava and Hystrix. Sometimes the problem will occurs.
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
