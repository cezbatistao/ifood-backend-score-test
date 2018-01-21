package ifood.score.support;

import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    
}
