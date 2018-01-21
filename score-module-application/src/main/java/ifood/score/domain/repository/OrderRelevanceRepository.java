package ifood.score.domain.repository;

import ifood.score.domain.model.OrderRelevance;
import ifood.score.domain.model.RelevanceCategory;
import ifood.score.domain.model.RelevanceMenuItem;
import ifood.score.domain.repository.entity.OrderRelevanceMongo;
import ifood.score.domain.repository.entity.RelevanceCategoryMongo;
import ifood.score.domain.repository.entity.RelevanceMenuItemMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderRelevanceRepository {

    private ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    public OrderRelevanceRepository(ReactiveMongoOperations reactiveMongoOperations) {
        this.reactiveMongoOperations = reactiveMongoOperations;
    }

    public Mono<OrderRelevance> save(OrderRelevance orderRelevance) {
        Mono<OrderRelevanceMongo> entity = reactiveMongoOperations.save(mapper(orderRelevance));
        return entity.map(this::mapper);
    }

    private OrderRelevanceMongo mapper(OrderRelevance orderRelevance) {
        List<RelevanceMenuItemMongo> relevanceMenuItensMongo = orderRelevance.getRelevancesMenuItem().stream()
                .map(r -> new RelevanceMenuItemMongo(r.getMenuUuid(), r.getRelevance()))
                .collect(Collectors.toList());
        List<RelevanceCategoryMongo> relevanceCategoriesMongo = orderRelevance.getRelevancesCategory().stream()
                .map(r -> new RelevanceCategoryMongo(r.getCategory(), r.getRelevance()))
                .collect(Collectors.toList());

        return new OrderRelevanceMongo(orderRelevance.getOrderUuid(), relevanceMenuItensMongo, relevanceCategoriesMongo);
    }

    private OrderRelevance mapper(OrderRelevanceMongo entity) {
        List<RelevanceMenuItem> relevanceMenuItens = entity.getRelevancesMenuItem().stream()
                .map(r -> new RelevanceMenuItem(r.getMenuUuid(), r.getRelevance()))
                .collect(Collectors.toList());
        List<RelevanceCategory> relevanceCategories = entity.getRelevancesCategory().stream()
                .map(r -> new RelevanceCategory(r.getCategory(), r.getRelevance()))
                .collect(Collectors.toList());

        return new OrderRelevance(entity.getOrderUuid(), relevanceMenuItens, relevanceCategories);
    }

    public Flux<OrderRelevance> findAll() {
        return reactiveMongoOperations.findAll(OrderRelevanceMongo.class).map(this::mapper);
    }
}
