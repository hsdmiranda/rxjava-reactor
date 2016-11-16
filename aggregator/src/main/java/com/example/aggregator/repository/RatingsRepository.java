package com.example.aggregator.repository;

import com.example.aggregator.client.OLGHttpClient;
import com.example.aggregator.client.dto.ProductRatingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import rx.Observable;

@Repository
public class RatingsRepository {

    @Autowired
    private OLGHttpClient olgHttpClient;

    public Observable<ProductRatingDto> ratingModel(String productId) {
        return olgHttpClient.getRatings(productId);
    }

    public Mono<ProductRatingDto> ratingModelReactor(String id) {
        return olgHttpClient.getRatingsReactor(id);
    }
}
