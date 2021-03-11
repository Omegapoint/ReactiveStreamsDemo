package se.omegapoint.reactivestreamsdemo.service;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Service
public class WebClientService
{
    private final WebClient httpClient;

    public WebClientService()
    {
        this.httpClient = createHttpClient();
    }

    private TcpClient tcpClient()
    {
        return TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(1000, TimeUnit.MILLISECONDS)));
    }

    private WebClient createHttpClient()
    {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient())))
                .build();
    }

    public WebClient httpClient()
    {
        return this.httpClient;
    }
}
