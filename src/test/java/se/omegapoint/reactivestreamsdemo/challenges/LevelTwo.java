package se.omegapoint.reactivestreamsdemo.challenges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import se.omegapoint.reactivestreamsdemo.service.FakeService;

import java.time.Duration;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LevelTwo
{
    private FakeService sut = new FakeService();

    @BeforeEach
    public void before()
    {
        this.sut = new FakeService();
    }

    @Test
    public void noWait()
    {
        var publisher = Flux.just("user1", "user2")
            .flatMap(s -> sut.archiveDataIfNeeded(s))
            // sut.deleteAllUserData
        ;

        StepVerifier.create(publisher)
            .verifyComplete();

        assertTrue(sut.userDataDeleted());
    }

    @Test
    public void expandYourMind()
    {
        var pageable = sut.firstPage();

        var publisher = pageable
            // sut.nextPage
            .takeWhile(song -> song.page < 10)
            .last()
        ;

        StepVerifier.create(publisher)
            .expectNextMatches(page -> page.page == 9)
            .verifyComplete();
    }

    @Test
    public void didSomeDigging()
    {
        Flux<String> offendingUsers = sut.getData("")
            // dig through files
            .filter(s -> s.contains("illegalDocument"))
            // parse
            ;

        StepVerifier.create(offendingUsers)
            .expectNextMatches(s -> sut.offendingUser(s))
            .verifyComplete();
    }

    @Test
    public void clearTheWay()
    {
        boolean[] blockingThread = new boolean[1];

        Mono<String> publisher = Mono.fromCallable(() -> {
            blockingThread[0] = Schedulers.isInNonBlockingThread();
            return "Hello, Omegapoint!";
        })
            //
            ;

        StepVerifier.create(publisher.subscribeOn(Schedulers.boundedElastic()))
            .expectNext("Hello, Omegapoint!")
            .verifyComplete();

        assertTrue(blockingThread[0], "The publisher ran on a blocking thread! This can clog the system");
    }

    @Test
    public void spareNoExpenses()
    {
        Flux<Integer> publisher = Flux.range(1, 7)
            .flatMap(integer ->
                    sut.slowThing(integer)
                //
            )
            .sort(Comparator.comparing(i -> i));

        Duration duration = StepVerifier.create(publisher)
            .expectNext(1, 2, 3, 4, 5, 6, 7)
            .verifyComplete();

        assertTrue(duration.toMillis() < 200);
    }

    @Test
    public void justOneSeq() {
        Flux<Integer> publisher = Flux.range(0, 10)
            // sut.variableThing
            ;

        StepVerifier.create(publisher)
            .expectNext(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            .verifyComplete();

        assertTrue(sut.numberOfTimesVariableThingCalled(10));
    }

    @Test
    public void illBeBack()
    {
        Mono<Boolean> processCheck = sut.checkIfProcessCompleted()
            //
            //
            ;

        StepVerifier.create(processCheck)
            .expectNext(true)
            .verifyComplete();

        assertTrue(sut.processCompleted());
    }
}
