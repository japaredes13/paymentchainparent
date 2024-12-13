package com.paymentchain.apigateway.setups;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.JsonNode;
import org.glassfish.jersey.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;

@Slf4j
@Component
public class AutenticationFiltering extends AbstractGatewayFilterFactory<AutenticationFiltering.Config> {

    public static class Config {
    }

    private final WebClient.Builder webClientBuilder;

    public AutenticationFiltering(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing  Authorization header");
            }


            String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
            String[] parts = authHeader.split(" ");
            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad Authorization structure");
            }

            return  webClientBuilder.build()
                    .get()
                    .uri("http://keycloack/roles").header(HttpHeaders.AUTHORIZATION, parts[1])
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .<ServerWebExchange>handle((response, sink) -> {
                        if(response != null){
                            log.info("See Objects: " + response);
                            //check for Partners rol
                            if(response.get("Partners") == null || response.get("Partners").asText().isEmpty()) {
                                sink.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Role Partners missing"));
                                return;
                            }
                        }else {
                            sink.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Roles missing"));
                            return;
                        }
                        sink.next(exchange);
                    })
                    .onErrorMap(error -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Communication Error", error.getCause());})
                    .flatMap(chain::filter);
        },1);
    }
}
