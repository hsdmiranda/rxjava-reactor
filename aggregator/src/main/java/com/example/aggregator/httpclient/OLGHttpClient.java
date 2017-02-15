package com.example.aggregator.httpclient;

import com.example.aggregator.httpclient.dto.ProductContentDto;
import com.example.aggregator.httpclient.dto.ProductRatingDto;
import com.example.aggregator.httpclient.dto.RatingsModel;
import com.example.aggregator.httpclient.dto.WishlistsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.ribbon.RequestTemplate;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientException;
import rx.Observable;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class OLGHttpClient {

    private static final String GROUP_NAME = "OLG";
    private static final Logger LOG = LoggerFactory.getLogger(OLGHttpClient.class);

    private HttpResourceGroup httpResourceGroup;

    private ObjectMapper mapper = new ObjectMapper();

    private HttpClient httpClient;

    public OLGHttpClient() {
        httpResourceGroup = Ribbon.createHttpResourceGroup(GROUP_NAME);
        httpClient = HttpClient.create("localhost",8081);
    }

    public Observable<WishlistsDto> getWishListRxNetty(String authorId) {
        HttpRequestTemplate<ByteBuf> templateGetWishlistBySocialListId = httpResourceGroup.newTemplateBuilder("OLG.GetWishlistByAuthorId", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/wishlist?author_id={author_id}")
                .withHeader("Accept", "application/json")
                .build();

        Observable<ByteBuf> request = templateGetWishlistBySocialListId.requestBuilder()
                .withRequestProperty("author_id", authorId)
                .build().toObservable();

        return request.defaultIfEmpty(new io.netty.buffer.EmptyByteBuf(ByteBufAllocator.DEFAULT))
                .map(byteBuf -> {
                    if (byteBuf.capacity() > 0) {
                        String json = byteBuf.toString(Charset.defaultCharset());
                        try {
                            ObjectReader reader = mapper.readerFor(WishlistsDto.class);
                            WishlistsDto model = reader.readValue(json);
                            return model;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return new WishlistsDto();
                    }
                });
    }

    @HystrixCommand(groupKey = "OLG", commandKey = "OLG.GetWishlistByAuthorId", observableExecutionMode = ObservableExecutionMode.LAZY)
    public Observable<WishlistsDto> getWishListReactorNetty(String authorId) {
        String url = "/wishlist?author_id={author_id}".replace("{author_id}", authorId);

        Mono<WishlistsDto> result = httpClient.get(url, r -> r
                .addHeader("Accept", "application/json"))
                .then(response -> response.receive().aggregate().asByteArray().otherwise(t -> Mono.error(t)))
                .otherwise(HttpClientException.class, e -> Mono.empty())
                .map(bytes -> {
                    String json = new String(bytes);
                    try {
                        ObjectReader reader = mapper.readerFor(WishlistsDto.class);
                        WishlistsDto model = reader.readValue(json);
                        return model;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        return RxConversion.toObservable(result);
    }


    public Observable<ProductRatingDto> getRatingsRxNetty(String productId) {
        RequestTemplate templateGetRatingsByGlobalId = httpResourceGroup.newTemplateBuilder("OLG.GetRatingsByGlobalId", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/ratings?product_id={product_id}")
                .withHeader("Accept", "application/json")
                .build();

        Observable<ByteBuf> request = templateGetRatingsByGlobalId.requestBuilder()
                .withRequestProperty("product_id", productId)
                .build().toObservable();


        return request.defaultIfEmpty(new EmptyByteBuf(ByteBufAllocator.DEFAULT))
                .flatMap(byteBuf -> {
                    if (byteBuf.capacity() > 0) {
                        String json = byteBuf.toString(Charset.defaultCharset());
                        try {
                            ObjectReader reader = mapper.readerFor(RatingsModel.class);
                            RatingsModel model = reader.readValue(json);
                            return Observable.from(model.getProducts());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return Observable.just(new ProductRatingDto());
                    }
                }).doOnError(throwable -> LOG.warn("OLG.getRatingsRxJava com.example.aggregator.client exception for productId " + productId, throwable));
    }

    @HystrixCommand(groupKey = "OLG", commandKey = "OLG.GetRatingsByGlobalId", observableExecutionMode = ObservableExecutionMode.LAZY)
    public Observable<ProductRatingDto> getRatingsReactorNetty(String productId) {
        String url = "/ratings?product_id={product_id}".replace("{product_id}", productId);

        Flux<ProductRatingDto> result = httpClient.get(url, r -> r
                .addHeader("Accept", "application/json"))
                .then(response -> response.receive().aggregate().asByteArray().otherwise(t -> Mono.error(t)))
                .otherwise(HttpClientException.class, e -> Mono.empty())
                .flatMap(bytes -> {
                    String json = new String(bytes);
                    try {
                        ObjectReader reader = mapper.readerFor(RatingsModel.class);
                        RatingsModel model = reader.readValue(json);
                        return Flux.fromIterable(model.getProducts());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        return RxConversion.toObservable(result);
    }

    public Observable<ProductContentDto> getProductRxNetty(String productId) {
        RequestTemplate templateGetRatingsByGlobalId = httpResourceGroup.newTemplateBuilder("OLG.GetProductsById", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/products?product_id={product_id}")
                .withHeader("Accept", "application/json")
                .build();

        Observable<ByteBuf> request = templateGetRatingsByGlobalId.requestBuilder()
                .withRequestProperty("product_id", productId)
                .build().toObservable();


        return request.defaultIfEmpty(new io.netty.buffer.EmptyByteBuf(ByteBufAllocator.DEFAULT))
                .map(byteBuf -> {
                    if (byteBuf.capacity() > 0) {
                        String json = byteBuf.toString(Charset.defaultCharset());
                        try {
                            ObjectReader reader = mapper.readerFor(ProductContentDto.class);
                            ProductContentDto model = reader.readValue(json);
                            return model;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return new ProductContentDto();
                    }
                });
    }

    @HystrixCommand(groupKey = "OLG", commandKey = "OLG.GetProductsById", observableExecutionMode = ObservableExecutionMode.LAZY)
    public Observable<ProductContentDto> getProductReactorNetty(String productId) {
        String url = "/products?product_id={product_id}".replace("{product_id}", productId);

        Mono<ProductContentDto> result = httpClient.get(url, r -> r
                .addHeader("Accept", "application/json"))
                .then(response -> response.receive().aggregate().asByteArray().otherwise(t -> Mono.error(t)))
                .otherwise(HttpClientException.class, e -> Mono.empty())
                .map(bytes -> {
                    String json = new String(bytes);
                    try {
                        ObjectReader reader = mapper.readerFor(ProductContentDto.class);
                        ProductContentDto model = reader.readValue(json);
                        return model;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        return RxConversion.toObservable(result);
    }

//    public Mono<WishlistsDto> getWishListReactor(String authorId) {
//        return RxConversion.toMono(getWishListRxNetty(authorId));
//    }
//
//    public Mono<ProductContentDto> getProductReactor(String productId) {
//        return RxConversion.toMono(getProductRxNetty(productId));
//    }
//
//    public Mono<ProductRatingDto> getRatingsReactor(String productId) {
//        return RxConversion.toMono(getRatingsRxNetty(productId));
//    }
}