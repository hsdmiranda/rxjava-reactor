package com.example.aggregator.httpclient;

import com.example.aggregator.httpclient.dto.ProductRatingDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;

public class RxConversion {
    public static <T> Flux<T> toFlux(Observable<T> observable) {
        return Flux.defer(() -> RxReactiveStreams.toPublisher(observable));
    }

    public static <T> Mono<T> toMono(Observable<T> observable) {
        return Mono.defer(() -> Mono.from(RxReactiveStreams.toPublisher(observable)));
    }

    public static <T> Observable<T> toObservable(Mono<T> mono) {
        return RxReactiveStreams.toObservable(mono);
    }

    public static <T> Observable<T> toObservable(Flux<T> result) {
        return RxReactiveStreams.toObservable(result);
    }
}
