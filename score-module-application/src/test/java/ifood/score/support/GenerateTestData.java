package ifood.score.support;

import ifood.score.domain.model.OrderRelevance;
import ifood.score.domain.model.RelevanceCategory;
import ifood.score.domain.model.RelevanceMenuItem;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;

public class GenerateTestData {

    public static Order generateTestOrder(List<Item> items) {
        return generateTestOrder(null, items);
    }

    public static Order generateTestOrder(Date confirmedAt, List<Item> items) {
        Order order = new Order();
        order.setUuid(UUID.randomUUID());
        order.setRestaurantUuid(UUID.randomUUID());
        order.setCustomerUuid(UUID.randomUUID());
        order.setAddressUuid(UUID.randomUUID());
        order.setConfirmedAt(confirmedAt);
        order.setItems(items);
        return order;
    }

    public static Menu generateTestMenu(Category category, BigDecimal unitPrice) {
        Menu menu = new Menu();
        menu.setCategory(category);
        menu.setUuid(UUID.randomUUID());
        menu.setUnitPrice(unitPrice);
        return menu;
    }

    public static Item generateTestItem(int quantity, Category category, UUID uuid, BigDecimal unitPrice) {
        Item item = new Item();
        item.setQuantity(quantity);
        item.setMenuCategory(category);
        item.setMenuUuid(uuid);
        item.setMenuUnitPrice(unitPrice);
        return item;
    }

    public static List<RelevanceMenuItem> generateTestRelevanceMenuItem(Pair<UUID, String>[] pairs) {
        List<RelevanceMenuItem> relevanceMenuItens = newArrayList();
        Arrays.stream(pairs).forEach(p-> {
            RelevanceMenuItem relevanceMenuItem = new RelevanceMenuItem(p.getLeft(), new BigDecimal(p.getRight()));
            relevanceMenuItens.add(relevanceMenuItem);
        });

        return relevanceMenuItens;
    }

    public static List<RelevanceCategory> generateTestRelevanceCategory(Pair<Category, String>[] pairs) {
        List<RelevanceCategory> relevanceCategories = newArrayList();
        Arrays.stream(pairs).forEach(p-> {
            RelevanceCategory relevanceCategory = new RelevanceCategory(p.getLeft(), new BigDecimal(p.getRight()));
            relevanceCategories.add(relevanceCategory);
        });

        return relevanceCategories;
    }

    public static OrderRelevance generateTestOrderRelevance(UUID orderUuid, Pair<UUID, String>[] pairsMenuItens, Pair<Category, String>[] pairsCategories) {
        List<RelevanceMenuItem> relevanceMenuItens = generateTestRelevanceMenuItem(pairsMenuItens);
        List<RelevanceCategory> relevanceCategories = generateTestRelevanceCategory(pairsCategories);

        return new OrderRelevance(orderUuid, relevanceMenuItens, relevanceCategories);
    }
}
