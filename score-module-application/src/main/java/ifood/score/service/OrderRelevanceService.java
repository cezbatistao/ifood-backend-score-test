package ifood.score.service;

import ifood.score.domain.model.*;
import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.domain.repository.ScoreRepository;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Verify.verify;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.groupingBy;

@Service
public class OrderRelevanceService {

    private OrderRelevanceRepository orderRelevanceRepository;
    private ScoreRepository scoreRepository;

    @Autowired
    public OrderRelevanceService(OrderRelevanceRepository orderRelevanceRepository, ScoreRepository scoreRepository) {
        this.orderRelevanceRepository = orderRelevanceRepository;
        this.scoreRepository = scoreRepository;
    }

    public Mono<OrderRelevance> calculateRelevance(Mono<Order> orderMono) {
        return orderMono.flatMap(o -> {
            OrderRelevance orderRelevance = this.calculateRelevance(o);
            return orderRelevanceRepository.save(orderRelevance);
        });
    }

    public Mono<Void> cancel(UUID orderUuid) {
        return orderRelevanceRepository.markCanceledByOrderUuid(orderUuid);
    }

    public Mono<Boolean> markOrdersAsExpired(UUID orderUuid) {
        return orderRelevanceRepository.markExpiredByConfirmedByOrderUuid(orderUuid);
    }

    protected OrderRelevance calculateRelevance(Order order) {
        verify(order != null, "Order is required to calculateRelevance.");
        verify(order.getItems() != null && !order.getItems().isEmpty(),
                "Order is required to calculateRelevance.");

        List<RelevanceMenuItem> relevanceMenuItems = calculateRelevanceMenuItem(order.getItems());
        List<RelevanceCategory> relevanceCategories = calculateRelevanceCategory(order.getItems());

        return new OrderRelevance(order.getUuid(), relevanceMenuItems, relevanceCategories);
    }

    protected List<RelevanceMenuItem> calculateRelevanceMenuItem(List<Item> itens) {
        Map<UUID, List<Item>> menuUuidCollected = itens.stream().collect(groupingBy(Item::getMenuUuid));
        int sumOfQuantities = itens.stream().mapToInt(Item::getQuantity).sum();
        double sumOfQuantityMultipliedUnitPrice = itens.stream().mapToDouble(it -> it.getQuantity() * it.getMenuUnitPrice().doubleValue()).sum();

        List<RelevanceMenuItem> relevancesMenuItem = newArrayList();
        menuUuidCollected.forEach((k, i) -> {
            Item item = itens.stream().filter(it -> it.getMenuUuid().equals(k)).findFirst().get();

            int quantityOfGroup = i.stream().mapToInt(Item::getQuantity).sum();

            double iq = (double) quantityOfGroup / sumOfQuantities;
            double ip = (item.getMenuUnitPrice().doubleValue() * item.getQuantity()) / sumOfQuantityMultipliedUnitPrice;

            RelevanceMenuItem relevanceMenuItem = new RelevanceMenuItem(k, BigDecimal.valueOf(Math.sqrt(iq * ip * 10000)).setScale(9, RoundingMode.HALF_UP));
            relevancesMenuItem.add(relevanceMenuItem);
        });

        return relevancesMenuItem;
    }

    protected List<RelevanceCategory> calculateRelevanceCategory(List<Item> itens) {
        Map<Category, List<Item>> menuUuidCollected = itens.stream().collect(groupingBy(Item::getMenuCategory));
        int sumOfQuantities = itens.stream().mapToInt(Item::getQuantity).sum();
        double sumOfQuantityMultipliedUnitPrice = itens.stream().mapToDouble(it -> it.getQuantity() * it.getMenuUnitPrice().doubleValue()).sum();

        List<RelevanceCategory> relevanceCategories = newArrayList();
        menuUuidCollected.forEach((k, i) -> {
            List<Item> categories = itens.parallelStream().filter(it -> it.getMenuCategory().equals(k)).collect(Collectors.toList());

            double quantityOfGroup = categories.stream().mapToDouble(Item::getQuantity).sum();
            double quantityMultipliedUnitPriceOfGroup = i.stream().mapToDouble(it -> it.getQuantity() * it.getMenuUnitPrice().doubleValue()).sum();

            double iq = quantityOfGroup / sumOfQuantities;
            double ip = quantityMultipliedUnitPriceOfGroup / sumOfQuantityMultipliedUnitPrice;

            RelevanceCategory relevanceCategory = new RelevanceCategory(k, BigDecimal.valueOf(Math.sqrt(iq * ip * 10000)).setScale(9, RoundingMode.HALF_UP));
            relevanceCategories.add(relevanceCategory);
        });

        return relevanceCategories;
    }

    protected Account calculateScore(List<OrderRelevance> orderRelevances) {
        List<ScoreMenuItem> scoreMenuItems = (List<ScoreMenuItem>) calculateAverageOfRelevance(orderRelevances.stream()
                .flatMap(r -> r.getRelevancesMenuItem().stream()));
        List<ScoreCategory> scoreCategories = (List<ScoreCategory>) calculateAverageOfRelevance(orderRelevances.stream()
                .flatMap(r -> r.getRelevancesCategory().stream()));

        return new Account(scoreMenuItems, scoreCategories);
    }

    private List<? extends Score> calculateAverageOfRelevance(Stream<Relevance> relevanceCategoryStream) {
        Map<Object, Double> groupByKey = relevanceCategoryStream
                .collect(groupingBy(Relevance::getKey, averagingDouble(r -> r.getRelevance().doubleValue())));

        List<Score> scores = newArrayList();
        groupByKey.forEach((k, v) -> {
            Score score = ScoreFactory.getScore(k, new BigDecimal(v).setScale(9, RoundingMode.HALF_UP));
            scores.add(score);
        });

        return scores;
    }

    public Mono<Void> calculateScore() {
        return scoreRepository.aggregateAvgMenuItemUuidByStatusActive()
                .then()
                .zipWith(scoreRepository.aggregateAvgCategoryUuidByStatusActive().then()).then();
    }

    public Mono<OrderRelevance> findById(UUID orderUuid) {
        return orderRelevanceRepository.findByOrderUuid(orderUuid);
    }
}
