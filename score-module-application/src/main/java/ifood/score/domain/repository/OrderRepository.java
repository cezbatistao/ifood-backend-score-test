package ifood.score.domain.repository;

import ifood.score.domain.repository.entity.ItemMongo;
import ifood.score.domain.repository.entity.OrderMongo;
import ifood.score.domain.repository.entity.StatusOrder;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Flux<Order> findAll() {
        return operations.findAll(OrderMongo.class).map(this::mapper);
    }

    public Flux<Order> findAllPresents() {
        return operations.find(query(where("status").is(StatusOrder.ACTIVE)), OrderMongo.class).map(this::mapper);
    }

    public Mono<Order> findByUuid(UUID orderUuid) {
        return operations.findOne(query(where("_id").is(orderUuid)), OrderMongo.class).map(this::mapper);
    }

    public Mono<Void> markCanceled(UUID orderUuid) {
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

    private OrderMongo mapper(Order order) {
        List<ItemMongo> itensMongo = order.getItems().stream()
                .map(i -> new ItemMongo(i.getQuantity(), i.getMenuUuid(), i.getMenuUnitPrice(), i.getMenuCategory()))
                .collect(Collectors.toList());

        OrderMongo orderMongo = new OrderMongo(order.getUuid(), order.getCustomerUuid(), order.getRestaurantUuid(),
                order.getAddressUuid(), itensMongo);
        orderMongo.setConfirmedAt(order.getConfirmedAt());

        return orderMongo;
    }

    private Order mapper(OrderMongo orderMongo) {
        List<Item> itens = orderMongo.getItems().stream()
                .map(i -> new Item(i.getQuantity(), i.getMenuUuid(), i.getMenuUnitPrice(), i.getMenuCategory()))
                .collect(Collectors.toList());

        return new Order(orderMongo.getUuid(), orderMongo.getCustomerUuid(), orderMongo.getRestaurantUuid(),
                orderMongo.getAddressUuid(), orderMongo.getConfirmedAt(), itens);
    }
}
