package se.omegapoint.reactivestreamsdemo.service;

import reactor.core.publisher.Mono;

public class MockedService
{
    public Mono<String> sendAnalyticsToDatabase(String input)
    {
        return Mono.just(input);
    }

    public Mono<String> enrich(String input)
    {
        return Mono.just("super " + input);
    }

    public Mono<Long> currentNanoTime()
    {
        return Mono.just(System.nanoTime());
    }
}
