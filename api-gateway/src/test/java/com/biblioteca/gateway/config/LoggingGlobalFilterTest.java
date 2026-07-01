package com.biblioteca.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingGlobalFilterTest {

    private LoggingGlobalFilter filter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new LoggingGlobalFilter();
        chain = Mockito.mock(GatewayFilterChain.class);
    }

    @Test
    @DisplayName("getOrder_retornaOrdenNegativoParaEjecucionTemprana")
    void getOrder_retornaOrdenNegativoParaEjecucionTemprana() {
        assertTrue(filter.getOrder() < 0, "Order should be negative to run before most filters");
    }

    @Test
    @DisplayName("filter_GET_retornaVoidYLogueaRequest")
    void filter_GET_retornaVoidYLogueaRequest() {
        MockServerHttpRequest request = MockServerHttpRequest.method(HttpMethod.GET, "/api/usuarios").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        response.setStatusCode(HttpStatus.OK);
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.OK);

        Mockito.when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        Mockito.verify(chain).filter(captor.capture());
        assertEquals("/api/usuarios", captor.getValue().getRequest().getURI().getPath());
    }

    @Test
    @DisplayName("filter_POST_pasaRequestAlChain")
    void filter_POST_pasaRequestAlChain() {
        MockServerHttpRequest request = MockServerHttpRequest.method(HttpMethod.POST, "/api/prestamos")
                .body("{\"usuarioId\":1,\"ejemplarId\":1}");
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Mockito.when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        Mockito.verify(chain, Mockito.times(1)).filter(exchange);
    }

    @Test
    @DisplayName("filter_con404Status_delegaYLoguea")
    void filter_con404Status_delegaYLoguea() {
        MockServerHttpRequest request = MockServerHttpRequest.method(HttpMethod.GET, "/api/libros/9999").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        Mockito.when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        Mockito.verify(chain, Mockito.times(1)).filter(exchange);
    }
}
