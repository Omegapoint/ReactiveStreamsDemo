package se.omegapoint.reactivestreamsdemo.tutorial;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;


public class Peeking
{
    @Test
    public void doOn()
    {
        Flux<String> dogs = Flux.just("Bulldog", "Golden Retriever", "Rottweiler");

        dogs
                .doOnSubscribe(subscription -> System.out.println("onSubscribe!"))
                .doOnNext(string -> System.out.println("onNext! " + string))
                .doOnEach(signal -> System.out.println("onEach! " + signal.getType() + " " + signal.get()))
                .doOnComplete(() -> System.out.println("onComplete!"))
                .doOnError(throwable -> System.out.println("onError! " + throwable))
                .doOnTerminate(() -> System.out.println("onTerminate!"))
                .subscribe();
    }

    @Test
    public void error()
    {
        Flux<String> dogs = Flux.just("Bulldog", "Golden Retriever", "Rottweiler");

        dogs
                .map(s -> 1 / 0)
                .doOnSubscribe(subscription -> System.out.println("onSubscribe!"))
                .doOnNext(string -> System.out.println("onNext! " + string))
                .doOnEach(signal -> System.out.println("onEach! " + signal.getType() + " " + signal.get()))
                .doOnComplete(() -> System.out.println("onComplete!"))
                .doOnError(throwable -> System.out.println("onError! " + throwable))
                .doOnTerminate(() -> System.out.println("onTerminate!"))
                .subscribe(integer -> {}, throwable -> {});
    }
}
