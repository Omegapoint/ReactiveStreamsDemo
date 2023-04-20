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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LevelOne
{
    private final Random random = new Random();
    private final int localValue = random.nextInt();
    private long lastCall = System.nanoTime();

    private FakeService sut;

    @BeforeEach
    public void before()
    {
        this.sut = new FakeService();
    }

    @Test
    public void multi() {
        Mono<Integer> publisher = Mono.just(5)
            //
            ;

        StepVerifier.create(publisher)
            .expectNext(localValue * 5)
            .verifyComplete();
    }

    @Test
    public void newAndImproved()
    {
        Mono<String> publisher = Mono.just("cat")
            // sut.enrich
            ;

        StepVerifier.create(publisher)
            .expectNextMatches(sut::isEnriched)
            .verifyComplete();
    }

    @Test
    public void dedupe()
    {
        var publisher = Flux.just("dog", "cat", "walrus", "cat", "horse", "cat", "seal")
            //
            .collectSortedList();

        List<String> expected = List.of("cat", "dog", "horse", "seal", "walrus");
        StepVerifier.create(publisher)
            .expectNext(expected)
            .verifyComplete();
    }

    @Test
    public void plain()
    {
        Mono<String> publisher = Mono.just("cat")
            .filter(s -> s.contains("super"))
            //
            ;

        StepVerifier.create(publisher)
            .verifyErrorMatches(throwable -> throwable.getMessage().equals("Not cool enough"));
    }

    @Test
    public void divideAndControl()
    {
        Mono<List<Integer>> publisher = Flux.range(0, 5)
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
        var publisher = Flux.range(0, 1000)
            //
            ;

        StepVerifier.create(publisher)
            .expectNext( 997, 998, 999)
            .verifyComplete();
    }

    @Test
    public void theEmperorOfChinaDislikesBar()
    {
        Flux<String> publisher = Flux.just("foo", "bar", "baz")
            //
            ;

        StepVerifier.create(publisher)
            .expectNext("foo")
            .expectNext("baz")
            .verifyComplete();
    }

    @Test
    public void mess()
    {
        Mono<String> publisher = Mono.just("foo")
            // sut.sendAnalyticsToDatabase
            .map(s -> s + "bar");

        StepVerifier.create(publisher)
            .expectNext("foobar")
            .verifyComplete();

        assertTrue(sut.analyticsSent());
    }

    @Test
    public void isCaring()
    {
        Mono<Long> data = sut.currentNanoTime()
            //
            ;

        Mono<List<Long>> publisher = Flux.range(0, 3)
            .concatMap(integer -> data)
            .collectList();

        StepVerifier.create(publisher)
            .expectNextMatches(longs -> new HashSet<>(longs).size() == 1)
            .verifyComplete();
    }

    @Test
    public void timeIsAFlatMap() {
        Mono<String> first = sut.firstPublisher();
        Mono<String> second = sut.secondPublisher();
        Mono<?> both = null;

        Duration timeTaken = StepVerifier.create(both)
            .expectNextCount(1L)
            .verifyComplete();

        assertTrue(sut.bothPublishersSubbedTo());
        assertTrue(timeTaken.toMillis() < 600, "Must complete in less than 600 ms");
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
        Map<String, Optional<String>> userConf = Map.of(
            "user1", Optional.of("easy"),
            "user2", Optional.empty(),
            "user3", Optional.of("hard"));

        return Mono.justOrEmpty(userConf.get(user));
    }

    @Test
    public void chill()
    {
        Flux<Integer> offendingUsers = Flux.range(1, 4)
            //
            .flatMap(integer -> rateLimited()
                .map(integer1 -> integer1 + integer));

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

    @Test
    public void onOff()
    {
        Mono<List<String>> data = Flux.range(0, 5)
            .flatMap(i -> Mono.defer(() -> sut.whatToEatToday(i))
                //
            )
            .collectList();

        StepVerifier.create(data)
            .expectNext(List.of("Super apple", "Fantastic banana", "Ultra orange", "Mega grape", "Hyper mango"))
            .verifyComplete();
    }
}
