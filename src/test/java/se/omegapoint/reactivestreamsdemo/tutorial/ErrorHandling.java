package se.omegapoint.reactivestreamsdemo.tutorial;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ErrorHandling
{
    @Test
    public void simpleError()
    {
        Mono<String> simpleError = Mono.error(new RuntimeException("An error"));

        simpleError
                .subscribe(System.out::println, System.out::println);
    }

    @Test
    public void fallback()
    {
        Mono<String> simpleError = Mono.error(new RuntimeException("An error"));

        simpleError
                .onErrorReturn("Fallback value")
                .subscribe(System.out::println, System.out::println);

        simpleError
                .onErrorResume(throwable -> Mono.just("Fallback value"))
                .subscribe(System.out::println, System.out::println);
    }

    @Test
    public void onErrorMap()
    {
        Mono<String> simpleError = Mono.error(new RuntimeException("Detailed technical domain exception"));

        simpleError
                .doOnError(throwable -> System.out.println("Something went wrong, act on it: " + throwable))
                .onErrorMap(throwable -> new RuntimeException("Client facing exception"))
                .subscribe(System.out::println, System.out::println);
    }

    @Test
    public void onErrorContinue()
    {
        Flux<String> fooBar = Flux.just("foo", "bar", "lorem", "ipsum");

        fooBar
                .map(s ->
                {
                    if (s.equals("bar"))
                        throw new RuntimeException("bar not allowed");
                    return s;
                })
                .onErrorContinue((throwable, o) -> System.out.println("Failed at: " + o + ". Due to: " + throwable.getMessage()))
                .doOnComplete(() -> System.out.println("Complete!"))
                .subscribe(System.out::println, System.out::println);

        /* AVOID THIS OPERATOR. Dependent on upstream operators supporting it */
    }

    @Test
    public void retry()
    {
        Mono<String> data = Mono.defer(this::getDataFromUnreliableServer);

        data
                .doOnError(throwable -> System.out.println("Failure: " + throwable.getMessage()))
                .retry()
                .subscribe(System.out::println, System.out::println);
    }

    private Mono<String> getDataFromUnreliableServer()
    {
        if (Math.random() > 0.97)
            return Mono.just("Requested data");
        return Mono.error(new RuntimeException("Timeout"));
    }
}
