package ifood.score.api.handler;

import ifood.score.domain.model.OrderRelevance;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.service.OrderRelevanceService;
import ifood.score.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class OrderHandler {

    private OrderService orderService;
    private OrderRelevanceService orderRelevanceService;

    @Autowired
    public OrderHandler(OrderService orderService, OrderRelevanceService orderRelevanceService) {
        this.orderService = orderService;
        this.orderRelevanceService = orderRelevanceService;
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        String value = request.pathVariable("orderUuid");
        UUID orderUuid = extractOrderUuidParam(value);
        if (orderUuid == null) {
            return ServerResponse.badRequest()
                    .body(Mono.just(String.format("Path parâmetro orderUuid inválido [%s].", value)), String.class);
        }

        return ServerResponse.ok().body(orderService.findById(orderUuid), Order.class);
    }

    public Mono<ServerResponse> getRelevances(ServerRequest request) {
        String value = request.pathVariable("orderUuid");
        UUID orderUuid = extractOrderUuidParam(value);
        if (orderUuid == null) {
            return ServerResponse.badRequest()
                    .body(Mono.just(String.format("Path parâmetro orderUuid inválido [%s].", value)), String.class);
        }

        return ServerResponse.ok().body(orderRelevanceService.findById(orderUuid), OrderRelevance.class);
    }

    protected UUID extractOrderUuidParam(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            return null;
        }
    }
}
