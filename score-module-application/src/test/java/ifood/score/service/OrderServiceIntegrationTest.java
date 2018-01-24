package ifood.score.service;

import ifood.score.domain.model.OrderRelevance;
import ifood.score.domain.model.RelevanceCategory;
import ifood.score.domain.model.RelevanceMenuItem;
import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.domain.repository.OrderRepository;
import ifood.score.domain.repository.entity.OrderMongo;
import ifood.score.domain.repository.entity.OrderRelevanceMongo;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;
import ifood.score.support.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static ifood.score.support.GenerateTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.array;
import static org.assertj.core.util.Lists.newArrayList;

public class OrderServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderRelevanceRepository orderRelevanceRepository;

    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;

    private Menu menuPizzaCheese;
    private Menu menuPizzaPepperoni;
    private Menu menuPizzaPortuguese;
    private Menu menuJapanese;
    private Menu menuArabicEsfihas;
    private Menu menuArabicKibe;
    private Menu menuVegan;
    private Menu menuHamburger;
    private Menu menuCoke;

    private Item itemPizzaCheeseOrder01;
    private Item itemJapaneseOrder01;
    private Item itemArabicEsfihasOrder01;
    private Item itemArabicKibeOrder01;
    private List<Item> itemsOrder01;
    private Order order01;

    private Item itemPizzaPortugueseOrder02;
    private Item itemVeganOrder02;
    private Item itemPizzaPepperoniOrder02;
    private Item itemArabicEsfihasOrder02;
    private Item itemPizzaCheeseOrder02;
    private List<Item> itemsOrder02;
    private Order order02;

    private Item itemHamburgerGourmetOrder03;
    private Item itemDietCokeOrder03;
    private List<Item> itemsOrder03;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        menuPizzaCheese = generateTestMenu(Category.PIZZA, new BigDecimal("20"));
        menuPizzaPepperoni = generateTestMenu(Category.PIZZA, new BigDecimal("23"));
        menuPizzaPortuguese = generateTestMenu(Category.PIZZA, new BigDecimal("26"));
        menuJapanese = generateTestMenu(Category.JAPANESE, new BigDecimal("8.9"));
        menuArabicEsfihas = generateTestMenu(Category.ARABIC, new BigDecimal("3.9"));
        menuArabicKibe = generateTestMenu(Category.ARABIC, new BigDecimal("5.5"));
        menuVegan = generateTestMenu(Category.VEGAN, new BigDecimal("3"));
        menuHamburger = generateTestMenu(Category.HAMBURGER, new BigDecimal("27.9"));
        menuCoke = generateTestMenu(Category.OTHER, new BigDecimal("4.5"));

        itemPizzaCheeseOrder01 = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        itemJapaneseOrder01 = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        itemArabicEsfihasOrder01 = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        itemArabicKibeOrder01 = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());
        itemsOrder01 = newArrayList(itemPizzaCheeseOrder01, itemJapaneseOrder01, itemArabicEsfihasOrder01, itemArabicKibeOrder01);
        order01 = generateTestOrder(itemsOrder01);

        itemPizzaPortugueseOrder02 = generateTestItem(1, menuPizzaPortuguese.getCategory(), menuPizzaPortuguese.getUuid(), menuPizzaPortuguese.getUnitPrice());
        itemVeganOrder02 = generateTestItem(3, menuVegan.getCategory(), menuVegan.getUuid(), menuVegan.getUnitPrice());
        itemPizzaPepperoniOrder02 = generateTestItem(1, menuPizzaPepperoni.getCategory(), menuPizzaPepperoni.getUuid(), menuPizzaPepperoni.getUnitPrice());
        itemArabicEsfihasOrder02 = generateTestItem(3, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        itemPizzaCheeseOrder02 = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        itemsOrder02 = newArrayList(itemPizzaPortugueseOrder02, itemVeganOrder02, itemPizzaPepperoniOrder02, itemArabicEsfihasOrder02, itemPizzaCheeseOrder02);
        order02 = generateTestOrder(itemsOrder02);

        itemHamburgerGourmetOrder03 = generateTestItem(1, menuHamburger.getCategory(), menuHamburger.getUuid(), menuHamburger.getUnitPrice());
        itemDietCokeOrder03 = generateTestItem(1, menuCoke.getCategory(), menuCoke.getUuid(), menuCoke.getUnitPrice());
        itemsOrder03 = newArrayList(itemHamburgerGourmetOrder03, itemDietCokeOrder03);

        reactiveMongoOperations.collectionExists(OrderMongo.class)
                .flatMap(exists -> exists ? reactiveMongoOperations.dropCollection(OrderMongo.class) : Mono.just(exists))
                .flatMap(o -> reactiveMongoOperations.createCollection(OrderMongo.class, CollectionOptions.empty()))
                .then()
                .block();

        reactiveMongoOperations.collectionExists(OrderRelevanceMongo.class)
                .flatMap(exists -> exists ? reactiveMongoOperations.dropCollection(OrderRelevanceMongo.class) : Mono.just(exists))
                .flatMap(o -> reactiveMongoOperations.createCollection(OrderRelevanceMongo.class, CollectionOptions.empty()))
                .then()
                .block();
    }

    @Test
    public void testSaveOrdersAndRelevances() {
        // given
        List<Order> ordersExpected = newArrayList(order01, order02);

        RelevanceMenuItem[] relevanceMenuItemsOrder01Expected = createDummyRelevanceMenuItensForOrder01(itemPizzaCheeseOrder01,
                itemJapaneseOrder01, itemArabicEsfihasOrder01, itemArabicKibeOrder01);
        RelevanceCategory[] relevanceCategoriesOrder01Expected = createDummyRelevanceCategoriesForOrder01();

        RelevanceMenuItem[] relevanceMenuItemsOrder02Expected = createDummyRelevanceMenuItemsForOrder02(itemPizzaPortugueseOrder02,
                itemVeganOrder02, itemPizzaPepperoniOrder02, itemArabicEsfihasOrder02, itemPizzaCheeseOrder02);
        RelevanceCategory[] relevanceCategoriesOrder02Expected = createDummyRelevanceCategoriesForOrder02();

        // when
        ordersExpected.forEach(o -> orderService.checkout(o).then().block());

        // then
        List<Order> ordersActual = orderRepository.findAllByStatusActive().collectList().block();

        assertThat(ordersActual).hasSize(ordersExpected.size()).containsAll(ordersExpected);

        // and
        List<OrderRelevance> orderRelevances = orderRelevanceRepository.findAllByStatusActive().collectList().block();

        assertThat(orderRelevances).hasSize(ordersExpected.size());
        assertThat(orderRelevances.stream().filter(o -> o.getOrderUuid().equals(order01.getUuid())).findFirst().get().getRelevancesMenuItem())
                .hasSize(relevanceMenuItemsOrder01Expected.length)
                .contains(relevanceMenuItemsOrder01Expected);
        assertThat(orderRelevances.stream().filter(o -> o.getOrderUuid().equals(order01.getUuid())).findFirst().get().getRelevancesCategory())
                .hasSize(relevanceCategoriesOrder01Expected.length)
                .contains(relevanceCategoriesOrder01Expected);
        assertThat(orderRelevances.stream().filter(o -> o.getOrderUuid().equals(order02.getUuid())).findFirst().get().getRelevancesMenuItem())
                .hasSize(relevanceMenuItemsOrder02Expected.length)
                .contains(relevanceMenuItemsOrder02Expected);
        assertThat(orderRelevances.stream().filter(o -> o.getOrderUuid().equals(order02.getUuid())).findFirst().get().getRelevancesCategory())
                .hasSize(relevanceCategoriesOrder02Expected.length)
                .contains(relevanceCategoriesOrder02Expected);
    }

    @Test
    public void testCancelOrdersAndRelevances() {
        // given
        newArrayList(order01, order02).forEach(o -> orderService.checkout(o).then().block());

        RelevanceMenuItem[] relevanceMenuItemsOrder01Expected = createDummyRelevanceMenuItensForOrder01(itemPizzaCheeseOrder01,
                itemJapaneseOrder01, itemArabicEsfihasOrder01, itemArabicKibeOrder01);
        RelevanceCategory[] relevanceCategoriesOrder01Expected = createDummyRelevanceCategoriesForOrder01();

        RelevanceMenuItem[] relevanceMenuItemsOrder02Expected = createDummyRelevanceMenuItemsForOrder02(itemPizzaPortugueseOrder02,
                itemVeganOrder02, itemPizzaPepperoniOrder02, itemArabicEsfihasOrder02, itemPizzaCheeseOrder02);
        RelevanceCategory[] relevanceCategoriesOrder02Expected = createDummyRelevanceCategoriesForOrder02();

        // when
        orderService.cancel(order01.getUuid()).block();

        // then
        List<Order> ordersActual = orderRepository.findAllByStatusActive().collectList().block();

        assertThat(ordersActual).hasSize(1).contains(order02).doesNotContain(order01);

        // and
        List<OrderRelevance> orderRelevances = orderRelevanceRepository.findAllByStatusActive().collectList().block();

        assertThat(orderRelevances).hasSize(1);
        assertThat(orderRelevances.get(0).getOrderUuid()).isEqualTo(order02.getUuid());

        assertThat(orderRelevances.stream().anyMatch(o -> o.getOrderUuid().equals(order01.getUuid()))).isFalse();
        assertThat(orderRelevances.stream().filter(o -> o.getOrderUuid().equals(order02.getUuid())).findFirst().get().getRelevancesMenuItem())
                .hasSize(relevanceMenuItemsOrder02Expected.length)
                .contains(relevanceMenuItemsOrder02Expected)
                .doesNotContain(relevanceMenuItemsOrder01Expected);
        assertThat(orderRelevances.stream().filter(o -> o.getOrderUuid().equals(order02.getUuid())).findFirst().get().getRelevancesCategory())
                .hasSize(relevanceCategoriesOrder02Expected.length)
                .contains(relevanceCategoriesOrder02Expected)
                .doesNotContain(relevanceCategoriesOrder01Expected);
    }

    @Test
    public void testExpirationDateOrdersAndRelevances() {
        // given
        Date confirmedAtOneMonthAgoOrder01 = Date.from(LocalDateTime.now().minusMonths(1).minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
        order01 = generateTestOrder(confirmedAtOneMonthAgoOrder01, itemsOrder01);

        Date confirmedAtTwoMonthAgoOrder02 = Date.from(LocalDateTime.now().minusMonths(2).atZone(ZoneId.systemDefault()).toInstant());
        order02 = generateTestOrder(confirmedAtTwoMonthAgoOrder02, itemsOrder02);

        Date confirmedAtOneWeekAgoOrder03 = Date.from(LocalDateTime.now().minusWeeks(1).atZone(ZoneId.systemDefault()).toInstant());
        Order order03 = generateTestOrder(confirmedAtOneWeekAgoOrder03, itemsOrder03);

        List<Order> orders = newArrayList(order01, order02, order03);

        orders.forEach(o -> orderService.checkout(o).then().block());

        RelevanceMenuItem[] relevanceMenuItemsOrder01Expected = createDummyRelevanceMenuItensForOrder01(itemPizzaCheeseOrder01,
                itemJapaneseOrder01, itemArabicEsfihasOrder01, itemArabicKibeOrder01);

        RelevanceCategory[] relevanceCategoriesOrder01Expected = createDummyRelevanceCategoriesForOrder01();

        RelevanceMenuItem[] relevanceMenuItemsOrder02Expected = createDummyRelevanceMenuItemsForOrder02(itemPizzaPortugueseOrder02,
                itemVeganOrder02, itemPizzaPepperoniOrder02, itemArabicEsfihasOrder02, itemPizzaCheeseOrder02);

        RelevanceCategory[] relevanceCategoriesOrder02Expected = createDummyRelevanceCategoriesForOrder02();

        RelevanceMenuItem[] relevanceMenuItemsOrder03Expected = createDummyRelevanceMenuItensForOrder03(itemHamburgerGourmetOrder03, itemDietCokeOrder03);

        RelevanceCategory[] relevanceCategoriesOrder03Expected = createDummyRelevanceCategoriesForOrder03();

        // when
        orderService.markOrdersAsExpired().block();

        // then
        List<Order> ordersActual = orderRepository.findAllByStatusActive().collectList().block();

        assertThat(ordersActual).hasSize(1).contains(order03).doesNotContain(order01, order02);

        // and
        List<OrderRelevance> orderRelevances = orderRelevanceRepository.findAllByStatusActive().collectList().block();

        assertThat(orderRelevances).hasSize(1);
        assertThat(orderRelevances.get(0).getOrderUuid()).isEqualTo(order03.getUuid());

        assertThat(orderRelevances.stream().anyMatch(o -> o.getOrderUuid().equals(order01.getUuid()) || o.getOrderUuid().equals(order02.getUuid()))).isFalse();

        assertThat(orderRelevances.stream().filter(o -> o.getOrderUuid().equals(order03.getUuid())).findFirst().get().getRelevancesMenuItem())
                .hasSize(relevanceMenuItemsOrder03Expected.length)
                .contains(relevanceMenuItemsOrder03Expected)
                .doesNotContain(relevanceMenuItemsOrder01Expected)
                .doesNotContain(relevanceMenuItemsOrder02Expected);

        assertThat(orderRelevances.stream().filter(o -> o.getOrderUuid().equals(order03.getUuid())).findFirst().get().getRelevancesCategory())
                .hasSize(relevanceCategoriesOrder03Expected.length)
                .contains(relevanceCategoriesOrder03Expected)
                .doesNotContain(relevanceCategoriesOrder01Expected)
                .doesNotContain(relevanceCategoriesOrder02Expected);
    }

    private RelevanceMenuItem[] createDummyRelevanceMenuItensForOrder01(Item itemPizzaCheeseOrder01, Item itemJapaneseOrder01,
                                                                        Item itemArabicEsfihasOrder01, Item itemArabicKibeOrder01) {
        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder01Expected = new RelevanceMenuItem(itemPizzaCheeseOrder01.getMenuUuid(), new BigDecimal("14.872457840"));
        RelevanceMenuItem relevanceMenuItemJapaneseOrder01Expected = new RelevanceMenuItem(itemJapaneseOrder01.getMenuUuid(), new BigDecimal("39.684667263"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder01Expected = new RelevanceMenuItem(itemArabicEsfihasOrder01.getMenuUuid(), new BigDecimal("26.269998228"));
        RelevanceMenuItem relevanceMenuItemArabicKibeOrder01Expected = new RelevanceMenuItem(itemArabicKibeOrder01.getMenuUuid(), new BigDecimal("15.598365377"));

        return array(relevanceMenuItemPizzaCheeseOrder01Expected, relevanceMenuItemJapaneseOrder01Expected, relevanceMenuItemArabicEsfihasOrder01Expected, relevanceMenuItemArabicKibeOrder01Expected);
    }

    private RelevanceCategory[] createDummyRelevanceCategoriesForOrder01() {
        RelevanceCategory relevanceCategoryPizzaOrder01Expected = new RelevanceCategory(Category.PIZZA, new BigDecimal("14.872457840"));
        RelevanceCategory relevanceCategoryJapaneseOrder01Expected = new RelevanceCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        RelevanceCategory relevanceCategoryArabicOrder01Expected = new RelevanceCategory(Category.ARABIC, new BigDecimal("42.013048183"));

        return array(relevanceCategoryPizzaOrder01Expected, relevanceCategoryJapaneseOrder01Expected, relevanceCategoryArabicOrder01Expected);
    }

    private RelevanceMenuItem[] createDummyRelevanceMenuItemsForOrder02(Item itemPizzaPortugueseOrder02, Item itemVeganOrder02,
                                                                        Item itemPizzaPepperoniOrder02, Item itemArabicEsfihasOrder02,
                                                                        Item itemPizzaCheeseOrder02) {
        RelevanceMenuItem relevanceMenuItemPizzaPortugueseOrder02Expected = new RelevanceMenuItem(itemPizzaPortugueseOrder02.getMenuUuid(), new BigDecimal("17.946063402"));
        RelevanceMenuItem relevanceMenuItemVeganOrder02Expected = new RelevanceMenuItem(itemVeganOrder02.getMenuUuid(), new BigDecimal("18.287923899"));
        RelevanceMenuItem relevanceMenuItemPizzaPepperoniOrder02Expected = new RelevanceMenuItem(itemPizzaPepperoniOrder02.getMenuUuid(), new BigDecimal("16.878989451"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder02Expected = new RelevanceMenuItem(itemArabicEsfihasOrder02.getMenuUuid(), new BigDecimal("20.851441406"));
        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder02Expected = new RelevanceMenuItem(itemPizzaCheeseOrder02.getMenuUuid(), new BigDecimal("15.739738822"));

        return array(relevanceMenuItemPizzaPortugueseOrder02Expected, relevanceMenuItemVeganOrder02Expected, relevanceMenuItemPizzaPepperoniOrder02Expected,
                relevanceMenuItemArabicEsfihasOrder02Expected, relevanceMenuItemPizzaCheeseOrder02Expected);
    }

    private RelevanceCategory[] createDummyRelevanceCategoriesForOrder02() {
        RelevanceCategory relevanceCategoryPizzaOrder02Expected = new RelevanceCategory(Category.PIZZA, new BigDecimal("50.636968354"));
        RelevanceCategory relevanceCategoryVeganOrder02Expected = new RelevanceCategory(Category.VEGAN, new BigDecimal("18.287923899"));
        RelevanceCategory relevanceCategoryArabicOrder02Expected = new RelevanceCategory(Category.ARABIC, new BigDecimal("20.851441406"));

        return array(relevanceCategoryPizzaOrder02Expected, relevanceCategoryVeganOrder02Expected, relevanceCategoryArabicOrder02Expected);
    }

    private RelevanceMenuItem[] createDummyRelevanceMenuItensForOrder03(Item itemHamburgerGourmetOrder03, Item itemDietCokeOrder03) {
        RelevanceMenuItem relevanceMenuItemHamburgerGourmetOrder03Expected = new RelevanceMenuItem(itemHamburgerGourmetOrder03.getMenuUuid(), new BigDecimal("65.616732283"));
        RelevanceMenuItem relevanceMenuItemDietCokeOrder03Expected = new RelevanceMenuItem(itemDietCokeOrder03.getMenuUuid(), new BigDecimal("26.352313835"));

        return array(relevanceMenuItemHamburgerGourmetOrder03Expected, relevanceMenuItemDietCokeOrder03Expected);
    }

    private RelevanceCategory[] createDummyRelevanceCategoriesForOrder03() {
        RelevanceCategory relevanceCategoryHamburgerOrder03Expected = new RelevanceCategory(Category.HAMBURGER, new BigDecimal("65.616732283"));
        RelevanceCategory relevanceCategoryOtherOrder03Expected = new RelevanceCategory(Category.OTHER, new BigDecimal("26.352313835"));

        return array(relevanceCategoryHamburgerOrder03Expected, relevanceCategoryOtherOrder03Expected);
    }
}
