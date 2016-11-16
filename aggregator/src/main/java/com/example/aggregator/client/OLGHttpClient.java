package com.example.aggregator.client;

import com.example.aggregator.client.dto.ProductContentDto;
import com.example.aggregator.client.dto.ProductRatingDto;
import com.example.aggregator.client.dto.RatingsModel;
import com.example.aggregator.client.dto.WishlistsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
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
import reactor.core.publisher.Mono;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class OLGHttpClient {

    private static final String GROUP_NAME = "OLG";
    private static final Logger LOG = LoggerFactory.getLogger(OLGHttpClient.class);

    private HttpResourceGroup httpResourceGroup;

    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        httpResourceGroup = Ribbon.createHttpResourceGroup(GROUP_NAME);
    }

    public Observable<WishlistsDto> getWishList(String authorId) {
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


    public Observable<ProductRatingDto> getRatings(String productId) {
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
                }).doOnError(throwable -> LOG.warn("OLG.getRatings com.example.aggregator.client exception for productId " + productId, throwable));
    }

    public Observable<ProductContentDto> getProduct(String productId) {
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

    public Mono<WishlistsDto> getWishListReactor(String authorId) {
        return RxConversion.toMono(getWishList(authorId));
    }

    public Mono<ProductContentDto> getProductReactor(String productId) {
        return RxConversion.toMono(getProduct(productId));
    }

    public Mono<ProductRatingDto> getRatingsReactor(String productId) {
        return RxConversion.toMono(getRatings(productId));
    }
}