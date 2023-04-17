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

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LevelZero
{
    @BeforeEach
    public void before()
    {
    }

    @Test
    public void helloWorld()
    {
        Mono<String> helloWorld = null;

        StepVerifier.create(helloWorld)
            .expectNext("Hello World!")
            .verifyComplete();
    }

    @Test
    public void unReactify()
    {
        var helloWorld = Mono.just("Hello World!")
            //
            ;

        assertEquals("Hello World", helloWorld);
    }

    @Test
    public void theVoid()
    {
        Mono<String> helloWorld = null;

        StepVerifier.create(helloWorld)
            .verifyComplete();
    }

    @Test
    public void separate()
    {
        List<String> hello = List.of("Hello", " ", "World", "!", " ", "My", " ", "name", " ", "is", " ", new String(new byte[7], StandardCharsets.UTF_8));

        Flux<String> helloWorld = null;

        StepVerifier.create(helloWorld)
            .expectNextSequence(hello)
            .verifyComplete();
    }

    @Test
    public void error()
    {
        RuntimeException error = new RuntimeException();
        Mono<String> helloWorld = null;

        StepVerifier.create(helloWorld)
            .verifyError(RuntimeException.class);
    }
}
