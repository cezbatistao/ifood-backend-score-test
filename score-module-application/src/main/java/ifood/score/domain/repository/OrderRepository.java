package ifood.score.domain.repository;

import ifood.score.domain.repository.entity.ItemMongo;
import ifood.score.domain.repository.entity.OrderMongo;
import ifood.score.domain.repository.entity.StatusOrder;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Repository
public class OrderRepository {

    private ReactiveMongoOperations operations;

    @Autowired
    public OrderRepository(ReactiveMongoOperations reactiveMongoOperations) {
        this.operations = reactiveMongoOperations;
    }

    public Mono<Order> save(Order order) {
        Mono<OrderMongo> orderMongoMono = operations.save(mapper(order));
        return orderMongoMono.map(this::mapper);
    }

    public Flux<Order> findAllByStatusActive() {
        return operations.find(query(where("status").is(StatusOrder.ACTIVE)), OrderMongo.class).map(this::mapper);
    }

    public Mono<Void> markCanceledByOrderUuid(UUID orderUuid) {
        return operations
                .updateFirst(query(where("_id").is(orderUuid)), update("status", StatusOrder.CANCELED), OrderMongo.class)
                .map(u -> {
                    if (u.getMatchedCount() == 0) {
                        Mono.error(new IllegalArgumentException(format("Order with UUID [%s] don't exists.", orderUuid)));
                    }

                    return u;
                })
                .then();
    }

    public Flux<UUID> findOrderUuidByConfirmedAtLessThanEqualAndStatus(Date confirmedAt, StatusOrder statusOrder) {
        Query query = query(where("confirmedAt").lte(confirmedAt).and("status").is(statusOrder));
        query.fields().include("_id");
        return operations.find(query, OrderMongo.class).map(OrderMongo::getUuid);
    }

    public Mono<Void> markExpiredByConfirmedAtLessThanEqual(Date confirmedAt) {
        return operations
                .updateMulti(
                        query(where("confirmedAt").lte(confirmedAt).and("status").is(StatusOrder.ACTIVE)),
                        update("status", StatusOrder.EXPIRED),
                        OrderMongo.class)
                .then();
    }

    private OrderMongo mapper(Order order) {
        List<ItemMongo> itensMongo = order.getItems().stream()
                .map(i -> new ItemMongo(i.getQuantity(), i.getMenuUuid(), i.getMenuUnitPrice().doubleValue(), i.getMenuCategory()))
                .collect(Collectors.toList());

        OrderMongo orderMongo = new OrderMongo(order.getUuid(), order.getCustomerUuid(), order.getRestaurantUuid(),
                order.getAddressUuid(), itensMongo);
        orderMongo.setConfirmedAt(order.getConfirmedAt());

        return orderMongo;
    }

    private Order mapper(OrderMongo orderMongo) {
        List<Item> itens = orderMongo.getItems().stream()
                .map(i -> new Item(i.getQuantity(), i.getMenuUuid(), BigDecimal.valueOf(i.getMenuUnitPrice()).setScale(9, RoundingMode.HALF_UP), i.getMenuCategory()))
                .collect(Collectors.toList());

        return new Order(orderMongo.getUuid(), orderMongo.getCustomerUuid(), orderMongo.getRestaurantUuid(),
                orderMongo.getAddressUuid(), orderMongo.getConfirmedAt(), itens);
    }
}
