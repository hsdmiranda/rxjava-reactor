package com.example.aggregator.repository;

import com.example.aggregator.client.OLGHttpClient;
import com.example.aggregator.client.dto.ProductContentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import rx.Observable;

@Repository
public class ProductContentRepository {

    @Autowired
    private OLGHttpClient olgHttpClient;

    public Observable<ProductContentDto> getProductContentDto(String productId) {
        return olgHttpClient.getProduct(productId);
    }

    public Mono<ProductContentDto> getProductContentDtoReactor(String id) {
        return olgHttpClient.getProductReactor(id);
    }
}
