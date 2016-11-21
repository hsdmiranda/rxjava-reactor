package com.example.aggregator.service;

import com.example.aggregator.httpclient.OLGHttpClient;
import com.example.aggregator.httpclient.dto.ProductContentDto;
import com.example.aggregator.httpclient.dto.ProductRatingDto;
import com.example.aggregator.httpclient.dto.WishlistDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;

public class WishlistService {

    private OLGHttpClient olgHttpClient;

    public WishlistService() {
        olgHttpClient = new OLGHttpClient();
    }

    public Observable<WishlistResponse> getWishlistsRxJava(String authorId) {
        return olgHttpClient.getWishListRxJava(authorId)
                .flatMap(wishlistsDto ->
                        Observable.from(wishlistsDto.getWishlists()).concatMap(wishlistDto -> composeWishlistRxJava(wishlistDto))
                                .toList())
                .concatMap(wishlistItemResponses -> {
                    WishlistResponse wishlistResponse = new WishlistResponse();
                    wishlistResponse.setWishlistItems(wishlistItemResponses);
                    return Observable.just(wishlistResponse);
        });
    }

    private Observable<WishlistItemResponse> composeWishlistRxJava(WishlistDto wishlistDto) {
        Observable<String> productsId = Observable.from(wishlistDto.getProductsId());

        return productsId.flatMap(id -> {
            Observable<ProductRatingDto> productRating = olgHttpClient.getRatingsRxJava(id);
            Observable<ProductContentDto> productContent = olgHttpClient.getProductRxJava(id);

            return Observable.zip(productRating, productContent, (pr, pc) -> {
                WishlistItemResponse wishlistItemResponse = new WishlistItemResponse();
                wishlistItemResponse.setProductId(id);
                wishlistItemResponse.setProductRating(pr.getAverageRating());
                wishlistItemResponse.setProductContent(pc.getName());
                return wishlistItemResponse;
            });
        });
    }

    public Flux<WishlistResponse> getWishlistsReactor(String authorId) {
        return olgHttpClient.getWishListReactor(authorId)
                .flatMap(wishlistsDto ->
                        Flux.fromIterable(wishlistsDto.getWishlists()).concatMap(wishlistDto -> composeWishlistReactor(wishlistDto))
                                .collectList())
                .concatMap(wishlistItemResponses -> {
                    WishlistResponse wishlistResponse = new WishlistResponse();
                    wishlistResponse.setWishlistItems(wishlistItemResponses);
                    return Mono.just(wishlistResponse);
                });
    }

    private Flux<WishlistItemResponse> composeWishlistReactor(WishlistDto wishlistDto) {
        Flux<String> productsId = Flux.fromIterable(wishlistDto.getProductsId());

        return productsId.flatMap(id -> {
            Mono<ProductRatingDto> productRating = olgHttpClient.getRatingsReactor(id);
            Mono<ProductContentDto> productContent = olgHttpClient.getProductReactor(id);

            return Flux.zip(productRating, productContent, (pr, pc) -> {
                WishlistItemResponse wishlistItemResponse = new WishlistItemResponse();
                wishlistItemResponse.setProductId(id);
                wishlistItemResponse.setProductRating(pr.getAverageRating());
                wishlistItemResponse.setProductContent(pc.getName());
                return wishlistItemResponse;
            });
        });
    }
}
