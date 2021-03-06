package ifood.score.domain.repository;

import ifood.score.domain.model.OrderRelevance;
import ifood.score.domain.model.RelevanceCategory;
import ifood.score.domain.model.RelevanceMenuItem;
import ifood.score.domain.repository.entity.OrderRelevanceMongo;
import ifood.score.domain.repository.entity.RelevanceCategoryMongo;
import ifood.score.domain.repository.entity.RelevanceMenuItemMongo;
import ifood.score.domain.repository.entity.StatusOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Repository
public class OrderRelevanceRepository {

    private static final String DOCUMENT_COLUMN_STATUS = "status";

    private ReactiveMongoOperations operations;

    @Autowired
    public OrderRelevanceRepository(ReactiveMongoOperations operations) {
        this.operations = operations;
    }

    public Mono<OrderRelevance> save(OrderRelevance orderRelevance) {
        Mono<OrderRelevanceMongo> entity = operations.save(mapper(orderRelevance));
        return entity.map(this::mapper);
    }

    public Mono<OrderRelevance> findByOrderUuid(UUID orderUuid) {
        return operations.findOne(query(where("_id").is(orderUuid)), OrderRelevanceMongo.class).map(this::mapper);
    }

    public Flux<OrderRelevance> findAllByStatusActive() {
        return operations.find(query(where(DOCUMENT_COLUMN_STATUS).is(StatusOrder.ACTIVE)), OrderRelevanceMongo.class).map(this::mapper);
    }

    public Mono<Void> markCanceledByOrderUuid(UUID orderUuid) {
        return operations
                .updateFirst(query(where("_id").is(orderUuid)), update(DOCUMENT_COLUMN_STATUS, StatusOrder.CANCELED), OrderRelevanceMongo.class)
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
                .updateMulti(query(where("_id").is(orderUuid)), update(DOCUMENT_COLUMN_STATUS, StatusOrder.EXPIRED), OrderRelevanceMongo.class)
                .map(u ->
                        u.getMatchedCount() > 0
                );
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
                .map(r -> new RelevanceMenuItem(r.getMenuUuid(), BigDecimal.valueOf(r.getRelevance()).setScale(9, RoundingMode.HALF_UP)))
                .collect(Collectors.toList());
        List<RelevanceCategory> relevanceCategories = entity.getRelevancesCategory().stream()
                .map(r -> new RelevanceCategory(r.getCategory(), BigDecimal.valueOf(r.getRelevance()).setScale(9, RoundingMode.HALF_UP)))
                .collect(Collectors.toList());

        return new OrderRelevance(entity.getOrderUuid(), relevanceMenuItens, relevanceCategories);
    }
}
