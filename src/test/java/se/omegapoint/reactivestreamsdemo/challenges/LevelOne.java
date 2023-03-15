package se.omegapoint.reactivestreamsdemo.challenges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import se.omegapoint.reactivestreamsdemo.service.MockedService;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LevelOne
{
    private final Random random = new Random();
    private final int localValue = random.nextInt();
    private final int externalValue = random.nextInt();
    private final Mono<Integer> externalRNG = Mono.just(externalValue);

    @Mock
    private Subscriber<String> subscriber;
    @Mock
    private MockedService sut = new MockedService();

    @BeforeEach
    public void before()
    {
        when(sut.enrich(anyString())).thenAnswer(invocation -> Mono.just("super " + invocation.getArgument(0)));
        when(sut.currentNanoTime()).thenAnswer(invocation -> Mono.defer(() -> Mono.just(System.nanoTime())));
        when(sut.sendAnalyticsToDatabase(anyString())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    @Test
    public void moreIsMore()
    {
        Mono<Integer> publisher = externalRNG
            //
            ;

        StepVerifier.create(publisher)
            .expectNext(externalValue + localValue)
            .verifyComplete();
    }

    @Test
    public void dunderKatt()
    {
        Mono<String> publisher = Mono.just("cat")
            //
            ;

        StepVerifier.create(publisher)
            .expectNext("super cat")
            .verifyComplete();

        Mockito.verify(sut, Mockito.times(1)).enrich(ArgumentMatchers.eq("cat"));
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

        Mockito.verify(sut, Mockito.times(1)).sendAnalyticsToDatabase(eq("foo"));
        Mockito.verify(subscriber).onSubscribe(any());
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
    void timeIsAFlatMap() {
        AtomicBoolean mono1Subscribed = new AtomicBoolean(false);
        AtomicBoolean mono2Subscribed = new AtomicBoolean(false);

        Mono<String> mono1 = Mono.just("Hello")
            .delayElement(Duration.ofMillis(500))
            .doOnSubscribe(subscription -> mono1Subscribed.set(true));

        Mono<String> mono2 = Mono.just(" world")
            .delayElement(Duration.ofMillis(500))
            .doOnSubscribe(subscription -> mono2Subscribed.set(true));

        Mono<String> twice = mono1.flatMap(s -> mono2);

        Instant start = Instant.now();
        StepVerifier.create(twice)
            .assertNext(result -> {
                assertTrue(Duration.between(start, Instant.now()).toMillis() < 1000, "onNext should not take the combined time of both Monos");
                assertTrue(mono1Subscribed.get(), "mono1 must be subscribed upon");
                assertTrue(mono2Subscribed.get(), "mono2 must be subscribed upon");
            })
            .verifyComplete();
    }
}
