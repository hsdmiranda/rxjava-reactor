package com.example.aggregator.repository;

import com.example.aggregator.client.OLGHttpClient;
import com.example.aggregator.client.dto.WishlistsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import rx.Observable;

@Repository
public class WishlistRepository {

    @Autowired
    private OLGHttpClient olgHttpClient;

    public Observable<WishlistsDto> getWishlist(String id) {
        return olgHttpClient.getWishList(id);
    }

    public Mono<WishlistsDto> getWishlistReactor(String authorId) {
        return olgHttpClient.getWishListReactor(authorId);
    }
}
