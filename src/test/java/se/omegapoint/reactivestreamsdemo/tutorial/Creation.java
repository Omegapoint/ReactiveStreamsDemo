package se.omegapoint.reactivestreamsdemo.tutorial;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Creation
{
    @Test
    public void create()
    {
        String hello = "hello";

        Mono<String> helloMono = Mono.just(hello);

        System.out.println(hello);
        System.out.println(helloMono);
    }

    @Test
    public void block()
    {
        String hello = "hello";

        Mono<String> helloMono = Mono.just(hello);

        System.out.println(hello);
        System.out.println(helloMono.block());
    }

    @Test
    public void subscribe()
    {
        String hello = "hello";

        Mono<String> helloMono = Mono.just(hello);

        System.out.println(hello);
        helloMono.subscribe(s -> System.out.println(s));
    }

    @Test
    public void hotPublisher()
    {
        Mono<Long> helloMono = Mono.just(System.nanoTime());

        helloMono.subscribe(s -> System.out.println(s));

        helloMono.subscribe(s -> System.out.println(s));
    }

    @Test
    public void coldPublisher()
    {
        Mono<Long> helloMono = Mono.defer(() -> Mono.just(System.nanoTime()));

        helloMono.subscribe(s -> System.out.println(s));

        helloMono.subscribe(s -> System.out.println(s));
    }

    @Test
    public void hotOrCold()
    {
        long nanoTime = System.nanoTime();

        Mono<Long> helloMono = Mono.defer(() -> Mono.just(nanoTime));

        helloMono.subscribe(s -> System.out.println(s));

        helloMono.subscribe(s -> System.out.println(s));
    }

    @Test
    public void coldToHot()
    {
        Mono<Long> cold = Mono.defer(() -> Mono.just(System.nanoTime()));

        Mono<Long> coldTurnedHot = cold.cache();

        cold.subscribe(s -> System.out.println(s));
        cold.subscribe(s -> System.out.println(s));
        System.out.println();
        coldTurnedHot.subscribe(s -> System.out.println(s));
        coldTurnedHot.subscribe(s -> System.out.println(s));
    }

    @Test
    public void nothingHappensUntilYouSubscribeQuestionMark()
    {
        long startTime = System.nanoTime();
        System.out.println("Method start " + ((System.nanoTime() - startTime) / 1000));

        Mono<String> largeMono = Mono.just(slowMethod("a"));
        System.out.println("Publisher created " + ((System.nanoTime() - startTime) / 1000));

        largeMono
            .doOnSubscribe(subscription -> System.out.println("Subscribe " + ((System.nanoTime() - startTime) / 1000)))
            .subscribe(s -> System.out.println("Success " + ((System.nanoTime() - startTime) / 1000)));

        System.out.println("Method end " + ((System.nanoTime() - startTime) / 1000));
    }

    private String slowMethod(String input)
    {
        while (!input.startsWith("b"))
        {
            char c = (char) (new Random().nextInt(26) + 'a');
            input = (input + c).substring(1);
        }
        return input;
    }

    @Test
    public void flux()
    {
        List<Integer> numberList = List.of(1, 2, 3, 4, 5);
        Stream<Integer> numberStream = List.of(1, 2, 3, 4, 5).stream();

        Flux<Integer> numbers1 = Flux.just(1, 2, 3, 4, 5);
        Flux<Integer> numbers2 = Flux.range(1, 5);
        Flux<Integer> numbers3 = Flux.fromIterable(numberList);
        Flux<Integer> numbers4 = Flux.fromStream(numberStream);

        numbers1.subscribe(System.out::println);
        System.out.println();
        numbers2.subscribe(System.out::println);
        System.out.println();
        numbers3.subscribe(System.out::println);
        System.out.println();
        numbers4.subscribe(System.out::println);
    }

    @Test
    public void empty()
    {
        Mono<String> emptyMono = Mono.empty();

        emptyMono.subscribe(System.out::println);
    }

    @Test
    public void error()
    {
        Mono<String> errorMono = Mono.error(new Exception("bad"));

        errorMono.subscribe(s -> System.out.println(s), throwable -> System.out.println(throwable));
    }

    @Test
    public void never()
    {
        Mono<String> neverMono = Mono.never();

        neverMono.subscribe(s -> System.out.println(s), throwable -> System.out.println(throwable));
    }

    @Test
    public void synchronousFluxCreation()
    {
        Flux<String> generatedFlux = Flux.generate(
                () -> "tic",
                (state, sink) ->
                {
                    sink.next(state);

                    return state.equals("tic") ? "toc" : "tic";
                }
        );

        generatedFlux                                               // Endless flux
                .take(Duration.of(10, ChronoUnit.MILLIS))   // Transforms Flux into new Flux which sends onComplete after 10 millisec
                .subscribe(System.out::println);
    }

    @Test
    public void asynchronousFluxCreation()
    {
        Flux<Long> createdFlux = Flux.create(sink ->
                {
                    List<Integer> items = List.of(1, 2, 3, 4, 5);

                    for (Integer integer : items)
                    {
                        Long item = Long.valueOf(integer);

                        sink.next(item);
                        sink.next(System.nanoTime());
                    }

                    sink.complete();
                }
        );

        createdFlux.subscribe(System.out::println);
    }
}
