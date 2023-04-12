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

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void expandYourMind() {
        var pageable = sut.firstPage();

        var publisher = pageable
            //
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
            //
            .filter(s -> s.contains("illegalDocument"))
            //
            ;

        StepVerifier.create(offendingUsers)
            .expectNextMatches(s -> sut.offendingUser(s))
            .verifyComplete();
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
