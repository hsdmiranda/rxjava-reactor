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

    public Flux<WishlistResponse> getWishlistsReactorNetty(String authorId) {
        return olgHttpClient.getWishListReactorNetty(authorId)
                .flatMap(wishlistsDto ->
                        Flux.fromIterable(wishlistsDto.getWishlists()).concatMap(wishlistDto -> composeWishlistReactorNetty(wishlistDto))
                                .collectList())
                .concatMap(wishlistItemResponses -> {
                    WishlistResponse wishlistResponse = new WishlistResponse();
                    wishlistResponse.setWishlistItems(wishlistItemResponses);
                    return Mono.just(wishlistResponse);
                });
    }

    private Flux<WishlistItemResponse> composeWishlistReactorNetty(WishlistDto wishlistDto) {
        Flux<String> productsId = Flux.fromIterable(wishlistDto.getProductsId());

        return productsId.flatMap(id -> {
            Flux<ProductRatingDto> productRating = olgHttpClient.getRatingsReactorNetty(id);
            Mono<ProductContentDto> productContent = olgHttpClient.getProductReactorNetty(id);

            return Flux.zip(productRating, productContent)
                    .map(tulpe2 -> {
                        WishlistItemResponse wishlistItemResponse = new WishlistItemResponse();
                        wishlistItemResponse.setProductId(id);
                        wishlistItemResponse.setProductRating(tulpe2.getT1().getAverageRating());
                        wishlistItemResponse.setProductContent(tulpe2.getT2().getName());
                        return wishlistItemResponse;
            });
        });
    }

    public Observable<WishlistResponse> getWishlistsReactorNettyAndHystrix(String authorId) {
		return olgHttpClient.getWishListRxNetty(authorId)
				.flatMap(wishlistsDto ->
						Observable.from(wishlistsDto.getWishlists()).concatMap(wishlistDto -> composeWishlistReactorNettyAndHystrix(wishlistDto))
								.toList())
				.concatMap(wishlistItemResponses -> {
					WishlistResponse wishlistResponse = new WishlistResponse();
					wishlistResponse.setWishlistItems(wishlistItemResponses);
					return Observable.just(wishlistResponse);
				});
    }

    private Observable<WishlistItemResponse> composeWishlistReactorNettyAndHystrix(WishlistDto wishlistDto) {
        Observable<String> productsId = Observable.from(wishlistDto.getProductsId());

        return productsId.flatMap(id -> {
            Observable<ProductRatingDto> productRating = olgHttpClient.getRatingsReactorNettyAndHystrix(id);
            Observable<ProductContentDto> productContent = olgHttpClient.getProductReactorNettyAndHystrix(id);

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
