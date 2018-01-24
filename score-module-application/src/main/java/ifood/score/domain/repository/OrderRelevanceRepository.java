package ifood.score.domain.repository;

import ifood.score.domain.model.OrderRelevance;
import ifood.score.domain.model.RelevanceCategory;
import ifood.score.domain.model.RelevanceMenuItem;
import ifood.score.domain.repository.entity.OrderRelevanceMongo;
import ifood.score.domain.repository.entity.RelevanceCategoryMongo;
import ifood.score.domain.repository.entity.RelevanceMenuItemMongo;
import ifood.score.domain.repository.entity.StatusOrder;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Repository
public class OrderRelevanceRepository {

    private ReactiveMongoOperations operations;

    @Autowired
    public OrderRelevanceRepository(ReactiveMongoOperations operations) {
        this.operations = operations;
    }

    public Mono<OrderRelevance> save(OrderRelevance orderRelevance) {
        Mono<OrderRelevanceMongo> entity = operations.save(mapper(orderRelevance));
        return entity.map(this::mapper);
    }

    public Flux<OrderRelevance> findAll() {
        return operations.findAll(OrderRelevanceMongo.class).map(this::mapper);
    }

    public Flux<OrderRelevance> findAllByStatusActive() {
        return operations.find(query(where("status").is(StatusOrder.ACTIVE)), OrderRelevanceMongo.class).map(this::mapper);
    }

    public Mono<Void> markCanceledByOrderUuid(UUID orderUuid) {
        return operations
                .updateFirst(query(where("_id").is(orderUuid)), update("status", StatusOrder.CANCELED), OrderRelevanceMongo.class)
                .map(u -> {
                    if (u.getMatchedCount() == 0) {
                        Mono.error(new IllegalArgumentException(format("Order with UUID [%s] don't exists.", orderUuid)));
                    }

                    return u;
                })
                .then();
    }

    public Mono<Boolean> markExpiredByConfirmedByOrderUuid(UUID orderUuid) {
        return operations
                .updateMulti(query(where("_id").is(orderUuid)), update("status", StatusOrder.EXPIRED), OrderRelevanceMongo.class)
                .map(u ->
                        u.getMatchedCount() > 0
                );
    }

    public Mono<Long> countByStatusActive() {
        return operations.count(query(where("status").is(StatusOrder.ACTIVE)), Long.class);
    }

    public Flux<UUID> getDistinctRelevanceMenuItemUuidByStatusActive() {
        Query query = query(where("status").is(StatusOrder.ACTIVE));
        query.fields().include("relevancesMenuItem.menuUuid").exclude("_id");

        return operations
                .find(query, OrderRelevanceMongo.class)
                .flatMap(o -> Flux.fromIterable(o.getRelevancesMenuItem().stream().map(RelevanceMenuItemMongo::getMenuUuid).distinct().collect(Collectors.toList())));
    }

    public Flux<Category> getDistinctRelevanceCategoryUuidByStatusActive() {
        Query query = query(where("status").is(StatusOrder.ACTIVE));
        query.fields().include("relevancesCategory.category").exclude("_id");
        return operations
                .find(query, OrderRelevanceMongo.class)
                .flatMap(o -> Flux.fromIterable(o.getRelevancesCategory().stream().map(RelevanceCategoryMongo::getCategory).distinct().collect(Collectors.toList())));
    }

    public Flux<OrderRelevance> findAllByMenuItemUuidAndStatusActive(UUID menuItemUuid) {
        return operations.find(query(where("status").is(StatusOrder.ACTIVE).and("relevancesMenuItem.menuUuid").is(menuItemUuid)), OrderRelevanceMongo.class).map(this::mapper);
    }

    public Flux<OrderRelevance> findallByCategoryAndStatusActive(Category category) {
        return operations.find(query(where("status").is(StatusOrder.ACTIVE).and("relevancesCategory.category").is(category)), OrderRelevanceMongo.class).map(this::mapper);
    }

    private OrderRelevanceMongo mapper(OrderRelevance orderRelevance) {
        List<RelevanceMenuItemMongo> relevanceMenuItensMongo = orderRelevance.getRelevancesMenuItem().stream()
                .map(r -> new RelevanceMenuItemMongo(r.getMenuUuid(), r.getRelevance().doubleValue()))
                .collect(Collectors.toList());
        List<RelevanceCategoryMongo> relevanceCategoriesMongo = orderRelevance.getRelevancesCategory().stream()
                .map(r -> new RelevanceCategoryMongo(r.getCategory(), r.getRelevance().doubleValue()))
                .collect(Collectors.toList());

        return new OrderRelevanceMongo(orderRelevance.getOrderUuid(), relevanceMenuItensMongo, relevanceCategoriesMongo);
    }

    private OrderRelevance mapper(OrderRelevanceMongo entity) {
        List<RelevanceMenuItem> relevanceMenuItens = entity.getRelevancesMenuItem().stream()
                .map(r -> new RelevanceMenuItem(r.getMenuUuid(), BigDecimal.valueOf(r.getRelevance()).setScale(9)))
                .collect(Collectors.toList());
        List<RelevanceCategory> relevanceCategories = entity.getRelevancesCategory().stream()
                .map(r -> new RelevanceCategory(r.getCategory(), BigDecimal.valueOf(r.getRelevance()).setScale(9)))
                .collect(Collectors.toList());

        return new OrderRelevance(entity.getOrderUuid(), relevanceMenuItens, relevanceCategories);
    }
}
