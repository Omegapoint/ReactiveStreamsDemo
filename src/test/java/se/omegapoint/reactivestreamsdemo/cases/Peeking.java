package se.omegapoint.reactivestreamsdemo.cases;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
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
