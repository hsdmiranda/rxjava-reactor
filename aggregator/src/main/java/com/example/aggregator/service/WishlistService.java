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
        return olgHttpClient.getWishListRxNetty(authorId)
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
            Observable<ProductRatingDto> productRating = olgHttpClient.getRatingsRxNetty(id);
            Observable<ProductContentDto> productContent = olgHttpClient.getProductRxNetty(id);

            return Observable.zip(productRating, productContent, (pr, pc) -> {
                WishlistItemResponse wishlistItemResponse = new WishlistItemResponse();
                wishlistItemResponse.setProductId(id);
                wishlistItemResponse.setProductRating(pr.getAverageRating());
                wishlistItemResponse.setProductContent(pc.getName());
                return wishlistItemResponse;
            });
        });
    }

    public Observable<WishlistResponse> getWishlistsReactorNetty(String authorId) {
        return olgHttpClient.getWishListReactorNetty(authorId)
                .flatMap(wishlistsDto ->
                        Observable.from(wishlistsDto.getWishlists()).concatMap(wishlistDto -> composeWishlistReactorNetty(wishlistDto))
                                .toList())
                .concatMap(wishlistItemResponses -> {
                    WishlistResponse wishlistResponse = new WishlistResponse();
                    wishlistResponse.setWishlistItems(wishlistItemResponses);
                    return Observable.just(wishlistResponse);
                });
    }

    private Observable<WishlistItemResponse> composeWishlistReactorNetty(WishlistDto wishlistDto) {
        Observable<String> productsId = Observable.from(wishlistDto.getProductsId());

        return productsId.flatMap(id -> {
            Observable<ProductRatingDto> productRating = olgHttpClient.getRatingsReactorNetty(id);
            Observable<ProductContentDto> productContent = olgHttpClient.getProductReactorNetty(id);

            return Observable.zip(productRating, productContent, (pr, pc) -> {
                WishlistItemResponse wishlistItemResponse = new WishlistItemResponse();
                wishlistItemResponse.setProductId(id);
                wishlistItemResponse.setProductRating(pr.getAverageRating());
                wishlistItemResponse.setProductContent(pc.getName());
                return wishlistItemResponse;
            });
        });
    }
}
