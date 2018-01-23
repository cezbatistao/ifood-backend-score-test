package ifood.score.service;

import ifood.score.domain.repository.OrderRepository;
import ifood.score.domain.repository.entity.StatusOrder;
import ifood.score.infrastructure.service.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private OrderRelevanceService orderRelevanceService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderRelevanceService orderRelevanceService) {
        this.orderRepository = orderRepository;
        this.orderRelevanceService = orderRelevanceService;
    }

    public Mono<Order> save(Order order) {
        Mono<Order> orderMono = orderRepository.save(order);
        return orderRelevanceService.calculateRelevance(orderMono).map(r -> order);
    }

    public Mono<Void> cancel(UUID orderUuid) {
        return orderRepository.markCanceledByOrderUuid(orderUuid).then(Mono.just(orderUuid)).flatMap(v->orderRelevanceService.cancel(orderUuid));
    }

    public Mono<Void> markOrdersAsExpired() {
        Date oneMonthAgo = Date.from(LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault()).toInstant());

//        reactiveMongoOperations.collectionExists(OrderRelevanceMongo.class)
//                .flatMap(exists -> exists ? reactiveMongoOperations.dropCollection(OrderRelevanceMongo.class) : Mono.just(exists))
//                .flatMap(o -> reactiveMongoOperations.createCollection(OrderRelevanceMongo.class, CollectionOptions.empty()))
//                .then()
//                .block();

        return orderRepository.findOrderUuidByConfirmedAtLessThanEqualAndStatus(oneMonthAgo, StatusOrder.ACTIVE)
                .flatMap(orderUuid-> orderRelevanceService.markOrdersAsExpired(orderUuid))
                .collectList()
                .then(orderRepository.markExpiredByConfirmedAtLessThanEqual(oneMonthAgo));

//        return orderRepository.findOrderUuidByConfirmedAtLessThanEqualAndStatus(oneMonthAgo, StatusOrder.ACTIVE)
//                .collectList()
//                .orderRelevanceService.markOrdersAsExpired(orderUuid).then().block();
//                .then(orderRepository.markExpiredByConfirmedAtLessThanEqual(oneMonthAgo));

//        orderRepository.markExpiredByConfirmedAtLessThanEqual(oneMonthAgo)
//                .and(orderRepository.findOrderUuidByConfirmedAtLessThanEqualAndStatus(oneMonthAgo, StatusOrder.ACTIVE));
//
//        orderRelevanceService.markOrdersAsExpired(orderUuid)
//
//        return orderRepository.findOrderUuidByConfirmedAtLessThanEqualAndStatus(oneMonthAgo, StatusOrder.ACTIVE).collectList()
//                .and()
//                .map(orderUuid-> orderRelevanceService.markOrdersAsExpired(orderUuid)).then();
    }
}
