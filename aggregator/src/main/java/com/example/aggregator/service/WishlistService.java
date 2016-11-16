package com.example.aggregator.service;

import com.example.aggregator.client.dto.ProductContentDto;
import com.example.aggregator.client.dto.ProductRatingDto;
import com.example.aggregator.client.dto.WishlistDto;
import com.example.aggregator.repository.ProductContentRepository;
import com.example.aggregator.repository.RatingsRepository;
import com.example.aggregator.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;

@Service
public class WishlistService {

    @Autowired
    private RatingsRepository ratingsRepository;

    @Autowired
    private ProductContentRepository productContentRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    public Observable<WishlistResponse> getWishlists(String authorId) {
        return wishlistRepository.getWishlist(authorId)
                .flatMap(wishlistsDto ->
                        Observable.from(wishlistsDto.getWishlists()).concatMap(wishlistDto -> composeWishlist(wishlistDto))
                                .toList())
                .concatMap(wishlistItemResponses -> {
                    WishlistResponse wishlistResponse = new WishlistResponse();
                    wishlistResponse.setWishlistItems(wishlistItemResponses);
                    return Observable.just(wishlistResponse);
        });
    }

    private Observable<WishlistItemResponse> composeWishlist(WishlistDto wishlistDto) {
        Observable<String> productsId = Observable.from(wishlistDto.getProductsId());

        return productsId.flatMap(id -> {
            Observable<ProductRatingDto> productRating = ratingsRepository.ratingModel(id);
            Observable<ProductContentDto> productContent = productContentRepository.getProductContentDto(id);

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
        return wishlistRepository.getWishlistReactor(authorId)
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
            Mono<ProductRatingDto> productRating = ratingsRepository.ratingModelReactor(id);
            Mono<ProductContentDto> productContent = productContentRepository.getProductContentDtoReactor(id);

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
