package se.omegapoint.reactivestreamsdemo.challenges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import se.omegapoint.reactivestreamsdemo.service.FakeService;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LevelOne
{
    private final Random random = new Random();
    private final int localValue = random.nextInt();
    private long lastCall = System.nanoTime();

    private final FakeService sut = new FakeService();

    @BeforeEach
    public void before()
    {
    }

    @Test
    public void multi() {
        Mono<Integer> publisher = Mono.just(5)
            //
            ;

        StepVerifier.create(publisher)
            .expectNext(50)
            .verifyComplete();
    }

    @Test
    public void newAndImproved()
    {
        Mono<String> publisher = Mono.just("cat")
            //
            ;

        StepVerifier.create(publisher)
            .expectNextMatches(sut::isEnriched)
            .verifyComplete();
    }

    @Test
    public void plainJane()
    {
        Mono<String> publisher = Mono.just("cat")
            .filter(s -> s.contains("super"))
            //
            ;

        StepVerifier.create(publisher)
            .verifyErrorMatches(throwable -> throwable.getMessage().equals("Not cool enough"));
    }

    @Test
    public void divideAnd()
    {
        Mono<List<Integer>> publisher = Flux.just(0, 1, 2, 3, 4)
            .flatMap(integer ->
                divide(10, integer)
                //
            )
            .collectList();

        StepVerifier.create(publisher)
            .expectNext(List.of(10, 5, 3, 2))
            .verifyComplete();
    }

    private Mono<Integer> divide(int num, int den)
    {
        if (den == 0)
        {
            if (num == 0)
            {
                return Mono.error(new RuntimeException("Undefined"));
            }
            return Mono.error(new RuntimeException("Divide by zero"));
        }
        return Mono.just(num/den);
    }

    @Test
    public void inTheEndItDoesReallyMatter()
    {
        Flux<Object> publisher = Flux.generate(() -> 0, (state, sink) -> {
            int next = random.nextInt(1000);
            sink.next(next);
            if (state == 1000) {
                sink.complete();
            }
            return state + 1;
        })
            //
            ;

        StepVerifier.create(publisher)
            .expectNextCount(3L)
            .verifyComplete();
    }

    @Test
    public void theEmperorOfChinaDislikesFoo()
    {
        Flux<String> publisher = Flux.just("foo", "bar", "baz")
            //
            ;

        StepVerifier.create(publisher)
            .expectNext("bar")
            .expectNext("baz")
            .verifyComplete();
    }

    @Test
    public void isCaring()
    {
        Mono<Long> data = sut.currentNanoTime()
            //
            ;

        Mono<List<Long>> publisher = Flux.just(1, 2, 3)
            .concatMap(integer -> data)
            .collectList();

        StepVerifier.create(publisher)
            .expectNextMatches(longs -> new HashSet<>(longs).size() == 1)
            .verifyComplete();
    }

    @Test
    public void mess()
    {
        Mono<String> publisher = Mono.just("foo")
            //
            .map(s -> s + "bar");

        StepVerifier.create(publisher)
            .expectNext("foobar")
            .verifyComplete();

        assertTrue(sut.analyticsSent());
    }

    @Test
    void timeIsAFlatMap() {
        AtomicBoolean mono1Subscribed = new AtomicBoolean(false);
        AtomicBoolean mono2Subscribed = new AtomicBoolean(false);

        Mono<String> mono1 = Mono.just("Hello")
            .delayElement(Duration.ofMillis(500))
            .doOnSubscribe(subscription -> mono1Subscribed.set(true));

        Mono<String> mono2 = Mono.just(" world")
            .delayElement(Duration.ofMillis(500))
            .doOnSubscribe(subscription -> mono2Subscribed.set(true));

        var both = mono1;

        Instant start = Instant.now();
        StepVerifier.create(both)
            .assertNext(result -> {
                assertTrue(Duration.between(start, Instant.now()).toMillis() < 1000, "onNext should not take the combined time of both Monos");
                assertTrue(mono1Subscribed.get(), "mono1 must be subscribed upon");
                assertTrue(mono2Subscribed.get(), "mono2 must be subscribed upon");
            })
            .verifyComplete();
    }

    @Test
    public void c()
    {
        Flux<String> hej = Flux.just("user1", "user2", "user3")
                .flatMap(s -> getUserConfig(s)
                    //
                )
            ;

        StepVerifier.create(hej)
            .expectNext("easy","default","hard")
            .verifyComplete();
    }

    private Mono<String> getUserConfig(String user)
    {
        Map<String, Optional<String>> userConf = Map.of("user1", Optional.of("easy"), "user2", Optional.empty(), "user3", Optional.of("hard"));
        return Mono.justOrEmpty(userConf.get(user));
    }

    @Test
    public void chill()
    {
        Flux<Integer> offendingUsers = Flux.just(1, 2, 3, 4)
            .flatMap(integer -> rateLimited()
                //
                .map(integer1 -> integer1 + integer)
            );

        StepVerifier.create(offendingUsers)
            .expectNextMatches(integer -> integer - 1 == localValue)
            .expectNextMatches(integer -> integer - 2 == localValue)
            .expectNextMatches(integer -> integer - 3 == localValue)
            .expectNextMatches(integer -> integer - 4 == localValue)
            .verifyComplete();
    }

    private Mono<Integer> rateLimited()
    {
        long time = System.nanoTime();
        if (time - this.lastCall < 1_000_000_000)
        {
            return Mono.error(new RuntimeException("Rate limit: 1 request per second"));
        }

        this.lastCall = time;
        return Mono.just(localValue);
    }
}
