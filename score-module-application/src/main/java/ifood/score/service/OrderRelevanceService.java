package ifood.score.service;

import ifood.score.domain.model.*;
import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.domain.repository.ScoreRepository;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Supplier;
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

    public OrderRelevance calculateRelevance(Order order) {
        verify(order != null, "Order is required to calculateRelevance.");
        verify(order.getItems() != null && !order.getItems().isEmpty(),
                "Order is required to calculateRelevance.");

        List<RelevanceMenuItem> relevanceMenuItems = newArrayList();
        order.getItems().stream().map(Item::getMenuUuid).forEach(menuUiid -> {
            BigDecimal relevance = calculateRelevanceMenuItem(order.getItems(), menuUiid);
            RelevanceMenuItem relevanceMenuItem = new RelevanceMenuItem(menuUiid, relevance);
            relevanceMenuItems.add(relevanceMenuItem);
        });

        List<RelevanceCategory> relevanceCategories = newArrayList();
        order.getItems().stream().map(Item::getMenuCategory).distinct().forEach(category -> {
            BigDecimal relevance = calculateRelevanceCategory(order.getItems(), category);
            RelevanceCategory relevanceCategory = new RelevanceCategory(category, relevance);
            relevanceCategories.add(relevanceCategory);
        });

        return new OrderRelevance(order.getUuid(), relevanceMenuItems, relevanceCategories);
    }

    public Account calculateScore(List<OrderRelevance> orderRelevances) {
        List<ScoreMenuItem> scoreMenuItems = (List<ScoreMenuItem>) calculateAverageOfRelevance(orderRelevances.stream()
                .flatMap(r -> r.getRelevancesMenuItem().stream()));
        List<ScoreCategory> scoreCategories = (List<ScoreCategory>) calculateAverageOfRelevance(orderRelevances.stream()
                .flatMap(r -> r.getRelevancesCategory().stream()));

        return new Account(scoreMenuItems, scoreCategories);
    }

    protected BigDecimal calculateRelevanceMenuItem(List<Item> itens, UUID menuUuid) {
        Optional<Item> itemOptional = itens.parallelStream().filter(i -> i.getMenuUuid().equals(menuUuid)).findFirst();
        if (!itemOptional.isPresent()) {
            return null;
        }

        Item item = itemOptional.get();

        double iq = (double) item.getQuantity() / itens.parallelStream().mapToInt(Item::getQuantity).sum();
        double ip = (item.getMenuUnitPrice().doubleValue() * item.getQuantity())
                / (itens.parallelStream().mapToDouble(i -> i.getMenuUnitPrice().doubleValue() * i.getQuantity()).sum());

        return BigDecimal.valueOf(Math.sqrt(iq * ip * 10000)).setScale(9, RoundingMode.HALF_UP);
    }

    protected BigDecimal calculateRelevanceCategory(List<Item> itens, Category category) {
        Supplier<Stream<Item>> itensSupplier = () -> itens.parallelStream().filter(i -> i.getMenuCategory().equals(category));

        Optional<Item> itemOptional = itensSupplier.get().findAny();
        if (!itemOptional.isPresent()) {
            return null;
        }

        double iq = (double) itensSupplier.get().parallel().mapToInt(Item::getQuantity).sum() / itens.parallelStream().mapToInt(Item::getQuantity).sum();
        double ip = (itensSupplier.get().parallel().mapToDouble(i -> i.getMenuUnitPrice().doubleValue() * i.getQuantity()).sum())
                / itens.parallelStream().mapToDouble(i -> i.getMenuUnitPrice().doubleValue() * i.getQuantity()).sum();

        return BigDecimal.valueOf(Math.sqrt(iq * ip * 10000)).setScale(9, RoundingMode.HALF_UP);
    }

    private List<ScoreMenuItem> calculateScoreMenuItens(List<RelevanceMenuItem> relevancesMenuItem) {
        Map<UUID, Double> groupByMenuItem = relevancesMenuItem.stream()
                .collect(groupingBy(RelevanceMenuItem::getMenuUuid, averagingDouble(r -> r.getRelevance().doubleValue())));

        List<ScoreMenuItem> scoreMenuItems = newArrayList();
        groupByMenuItem.forEach((k, v) -> {
            ScoreMenuItem scoreMenuItem = new ScoreMenuItem(k, new BigDecimal(v).setScale(9, RoundingMode.HALF_UP));
            scoreMenuItems.add(scoreMenuItem);
        });
        return scoreMenuItems;
    }

    private List<ScoreCategory> calculateScoreCategories(List<RelevanceCategory> relevanceCategories) {
        Map<Category, Double> groupByCategory = relevanceCategories.stream()
                .collect(groupingBy(RelevanceCategory::getCategory, averagingDouble(r -> r.getRelevance().doubleValue())));

        List<ScoreCategory> scoreCategories = newArrayList();
        groupByCategory.forEach((k, v) -> {
            ScoreCategory scoreCategory = new ScoreCategory(k, new BigDecimal(v).setScale(9, RoundingMode.HALF_UP));
            scoreCategories.add(scoreCategory);
        });
        return scoreCategories;
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
        return orderRelevanceRepository
                .findAllByStatusActive()
                .collectList()
                .map(this::calculateScore)
                .flatMap(account-> scoreRepository.saveAllScoreMenuItem(account.getScoreMenuItems())
                        .then(scoreRepository.saveAllCategory(account.getScoreCategories()).then()));
//        return orderRelevanceRepository.getDistinctRelevanceMenuItemUuidByStatusActive()
//                .flatMap(u -> orderRelevanceRepository.findAllByMenuItemUuidAndStatusActive(u))
//                .collectList()
//                .flatMapMany(orderRelevances -> {
//                    return Flux.fromIterable(orderRelevances.stream().map(OrderRelevance::getRelevancesMenuItem).distinct().flatMap(List::stream).collect(Collectors.toList()));
//                })
//                .collectList()
//                .flatMapMany(x-> {
//                    return Flux.just(this.calculateScoreMenuItens(x));
//                })
//                .flatMap(scoreMenuItems -> scoreRepository.saveAllScoreMenuItem(scoreMenuItems))
//                .thenMany(orderRelevanceRepository.getDistinctRelevanceCategoryUuidByStatusActive())
//                .flatMap(c-> orderRelevanceRepository.findallByCategoryAndStatusActive(c))
//                .collectList()
//                .flatMapMany(orderRelevances -> Flux.fromIterable(orderRelevances.stream().map(OrderRelevance::getRelevancesCategory).collect(Collectors.toList())))
//                .map(this::calculateScoreCategories)
//                .flatMap(scoreCategories -> scoreRepository.saveAllCategory(scoreCategories))
//                .then();
    }
}
