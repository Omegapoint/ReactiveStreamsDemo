package se.omegapoint.reactivestreamsdemo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.omegapoint.reactivestreamsdemo.domain.Lyrics;
import se.omegapoint.reactivestreamsdemo.exceptions.LyricException;

@Service
public class LyricsService
{
    private final WebClientService webClientService;
    private final String basePath = "https://api.lyrics.ovh/v1/";

    public LyricsService(WebClientService webClientService)
    {
        this.webClientService = webClientService;
    }

    public Flux<String> getLyricLines(String artist, String song)
    {
        return getLyrics(artist, song)
                .flatMapMany(lyrics -> Flux.fromIterable(lyrics.getLines()))
                .switchIfEmpty(Mono.error(new LyricException("Invalid song parameters")));
    }

    public Mono<Lyrics> getLyrics(String artist, String song)
    {
        String path = basePath + artist + "/" + song;

        return webClientService.httpClient()
                .get()
                .uri(path)
                .retrieve()
                .bodyToMono(Lyrics.class)
                .onErrorResume(throwable -> Mono.empty());
    }
}
