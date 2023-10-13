package org.brain.api_gateway.filter;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final WebClient.Builder webClientBuilder;

    public AuthenticationFilter(RouteValidator validator, WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.validator = validator;
        this.webClientBuilder = webClientBuilder;
    }

    @Value("${auth.validate.token.path}")
    private String AUTH_VALIDATE_TOKEN_URL;


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("Checking request - {}", exchange.getRequest().getPath());
            if (validator.isSecured.test(exchange.getRequest())) {
                log.info("Checking secured request - {}", exchange.getRequest().getPath());
                return handleSecureRequest(exchange, chain);
            }
            log.info("Request passed - {}", exchange.getRequest().getPath());
            return chain.filter(exchange);
        });
    }

    private Mono<Void> handleSecureRequest(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (hasAuthorizationBearerInHeader(exchange.getRequest())) {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            return sendValidateTokenRequest(authHeader)
                    .flatMap(response -> handleTokenValidationResponse(response, exchange, chain))
                    .onErrorResume(e -> {
                                log.error("Token not valid in request - {}", exchange.getRequest().getPath());
                                return onError(exchange, HttpStatus.UNAUTHORIZED);
                            }
                    );
        } else {
            log.error("No token in AUTHORIZATION header, request - {}", exchange.getRequest().getPath());
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean hasAuthorizationBearerInHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private Mono<ResponseEntity<Void>> sendValidateTokenRequest(String authHeader) {
        return webClientBuilder.build()
                .post()
                .uri(AUTH_VALIDATE_TOKEN_URL)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve().toEntity(Void.class);
    }

    private Mono<Void> handleTokenValidationResponse(ResponseEntity<Void> response, ServerWebExchange exchange, GatewayFilterChain chain) {
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Request passed - {}", exchange.getRequest().getPath());
            return chain.filter(exchange);
        } else {
            log.info("Token not valid in request - {}", exchange.getRequest().getPath());
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    @NoArgsConstructor
    public static class Config {
    }
}
