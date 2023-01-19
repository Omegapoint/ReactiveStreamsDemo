package se.omegapoint.reactivestreamsdemo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.omegapoint.reactivestreamsdemo.domain.Lyrics;

@Service
public class AmazingLyricGeneratorService
{
    private final LyricsService lyricsService;
    private final QuoteService quoteService;

    public AmazingLyricGeneratorService(LyricsService lyricsService, QuoteService quoteService)
    {
        this.lyricsService = lyricsService;
        this.quoteService = quoteService;
    }

    public Mono<Lyrics> getAmazingSongText(String artist, String song)
    {
        if (song.contains("_"))
        {
            throw new RuntimeException("Crashed due to underline character. Sensitive info!");
        }

        return lyricsService.getLyricLines(artist, song)
                .flatMapSequential(lyricLine ->
                        quoteService.getRandomTaylorSwiftQuote()
                                .flatMapMany(quote -> Flux.just(quote.getQuote(), lyricLine))
                )
                .filter(this::removeBadLines)
                .collectList()
                .map(Lyrics::fromLines);
    }

    private boolean removeBadLines(String lyricLine)
    {
        return !lyricLine.contains("the");
    }
}
