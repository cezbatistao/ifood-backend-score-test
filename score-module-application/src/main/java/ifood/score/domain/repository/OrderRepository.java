package ifood.score.domain.repository;

import ifood.score.domain.repository.entity.ItemMongo;
import ifood.score.domain.repository.entity.OrderMongo;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {

    private ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    public OrderRepository(ReactiveMongoOperations reactiveMongoOperations) {
        this.reactiveMongoOperations = reactiveMongoOperations;
    }

    public Mono<Order> save(Order order) {
        Mono<OrderMongo> orderMongoMono = reactiveMongoOperations.save(mapper(order));
        return orderMongoMono.map(this::mapper);
    }

    public Flux<Order> findAll() {
        return reactiveMongoOperations.findAll(OrderMongo.class).map(this::mapper);
    }

    private OrderMongo mapper(Order order) {
        List<ItemMongo> itensMongo = order.getItems().stream()
                .map(i -> new ItemMongo(i.getQuantity(), i.getMenuUuid(), i.getMenuUnitPrice(), i.getMenuCategory()))
                .collect(Collectors.toList());

        return new OrderMongo(order.getUuid(), order.getCustomerUuid(), order.getRestaurantUuid(),
                order.getAddressUuid(), order.getConfirmedAt(), itensMongo);
    }

    private Order mapper(OrderMongo orderMongo) {
        List<Item> itens = orderMongo.getItems().stream()
                .map(i -> new Item(i.getQuantity(), i.getMenuUuid(), i.getMenuUnitPrice(), i.getMenuCategory()))
                .collect(Collectors.toList());

        return new Order(orderMongo.getUuid(), orderMongo.getCustomerUuid(), orderMongo.getRestaurantUuid(),
                orderMongo.getAddressUuid(), orderMongo.getConfirmedAt(), itens);
    }
}
