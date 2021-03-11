package se.omegapoint.reactivestreamsdemo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import se.omegapoint.reactivestreamsdemo.domain.Authentication;

@Service
public class AuthService
{
    public Mono<Authentication> apiKeyValid(String apiKey)
    {
        boolean validKey = apiKey.equals("abc");

        if (validKey)
            return Mono.just(Authentication.granted());

        return Mono.just(Authentication.notGranted());
    }
}
