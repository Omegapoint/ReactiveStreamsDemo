package se.omegapoint.reactivestreamsdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.omegapoint.reactivestreamsdemo.exceptions.ApiException;
import se.omegapoint.reactivestreamsdemo.exceptions.LyricException;
import se.omegapoint.reactivestreamsdemo.json.LyricsApi;
import se.omegapoint.reactivestreamsdemo.service.AmazingLyricGeneratorService;
import se.omegapoint.reactivestreamsdemo.service.AuthService;

import java.util.function.Function;

@RestController
public class LyricsController
{
    private final AmazingLyricGeneratorService amazingLyricGeneratorService;
    private final AuthService authService;

    public LyricsController(AmazingLyricGeneratorService amazingLyricGeneratorService, AuthService authService)
    {
        this.amazingLyricGeneratorService = amazingLyricGeneratorService;
        this.authService = authService;
    }

    @GetMapping(path = "apikey/{apiKey}/artist/{artist}/song/{song}")
    public Mono<ResponseEntity<?>> getAmazingText(
            @PathVariable("apiKey") String apiKey,
            @PathVariable("artist") String artist,
            @PathVariable("song") String song)
    {
        // apiKey is "abc"
        // Don't use underline char in songs!

        return authService.apiKeyValid(apiKey)
                .flatMap(authentication ->
                {
                    if (authentication.isAuthenticated())
                        return amazingLyricGeneratorService.getAmazingSongText(artist, song)
                                .map(lyrics -> ResponseEntity.ok(LyricsApi.fromDomain(lyrics)));

                    return Mono.just(ResponseEntity.status(401).build());
                })
                .onErrorMap(handleError())
                .onErrorResume(wrapError());
    }

    private Function<Throwable, ? extends Throwable> handleError()
    {
        return throwable ->
        {
            if (throwable instanceof LyricException)
            {
                System.out.println("Logged an error: " + throwable.getMessage()); // Pretend this is logging
                return new ApiException("Not found", 404);
            }
            System.out.println("Logged an error: " + throwable.getMessage()); // Pretend this is logging
            return new ApiException("Internal server error", 500);
        };
    }

    private Function<? super Throwable, Mono<ResponseEntity<?>>> wrapError()
    {
        return e -> Mono.just(
                ResponseEntity
                        .status(((ApiException) e).getHttpCode())
                        .body(e.getMessage())
        );
    }
}
