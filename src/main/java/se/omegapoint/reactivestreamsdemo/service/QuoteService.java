package se.omegapoint.reactivestreamsdemo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.omegapoint.reactivestreamsdemo.domain.Lyrics;
import se.omegapoint.reactivestreamsdemo.domain.Quote;

@Service
public class QuoteService
{
    private final WebClientService webClientService;

    public QuoteService(WebClientService webClientService)
    {
        this.webClientService = webClientService;
    }

    public Mono<Quote> getRandomTaylorSwiftQuote()
    {
        String path = "https://api.taylor.rest";

        return webClientService.httpClient()
                .get()
                .uri(path)
                .retrieve()
                .bodyToMono(Quote.class);
    }
}
