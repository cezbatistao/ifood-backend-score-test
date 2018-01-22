package ifood.score.service;

import ifood.score.domain.model.OrderRelevance;
import ifood.score.domain.model.RelevanceCategory;
import ifood.score.domain.model.RelevanceMenuItem;
import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.domain.repository.OrderRepository;
import ifood.score.domain.repository.entity.OrderMongo;
import ifood.score.domain.repository.entity.OrderRelevanceMongo;
import ifood.score.infrastructure.config.Application;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static ifood.score.support.GenerateTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {

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
        Item itemPizzaCheeseOrder01 = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        Item itemJapaneseOrder01 = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        Item itemArabicEsfihasOrder01 = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemArabicKibeOrder01 = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());

        List<Item> itemsOrder01 = newArrayList(itemPizzaCheeseOrder01, itemJapaneseOrder01, itemArabicEsfihasOrder01, itemArabicKibeOrder01);

        Order order01 = generateTestOrder(itemsOrder01);

        Item itemPizzaPortugueseOrder02 = generateTestItem(1, menuPizzaPortuguese.getCategory(), menuPizzaPortuguese.getUuid(), menuPizzaPortuguese.getUnitPrice());
        Item itemVeganOrder02 = generateTestItem(3, menuVegan.getCategory(), menuVegan.getUuid(), menuVegan.getUnitPrice());
        Item itemPizzaPepperoniOrder02 = generateTestItem(1, menuPizzaPepperoni.getCategory(), menuPizzaPepperoni.getUuid(), menuPizzaPepperoni.getUnitPrice());
        Item itemArabicEsfihasOrder02 = generateTestItem(3, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemPizzaCheeseOrder02 = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());

        List<Item> itemsOrder02 = newArrayList(itemPizzaPortugueseOrder02, itemVeganOrder02, itemPizzaPepperoniOrder02,
                itemArabicEsfihasOrder02, itemPizzaCheeseOrder02);

        Order order02 = generateTestOrder(itemsOrder02);

        List<Order> ordersExpected = newArrayList(order01, order02);

        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder01Expected = new RelevanceMenuItem(itemPizzaCheeseOrder01.getMenuUuid(), new BigDecimal("14.872457840"));
        RelevanceMenuItem relevanceMenuItemJapaneseOrder01Expected = new RelevanceMenuItem(itemJapaneseOrder01.getMenuUuid(), new BigDecimal("39.684667263"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder01Expected = new RelevanceMenuItem(itemArabicEsfihasOrder01.getMenuUuid(), new BigDecimal("26.269998228"));
        RelevanceMenuItem relevanceMenuItemArabicKibeOrder01Expected = new RelevanceMenuItem(itemArabicKibeOrder01.getMenuUuid(), new BigDecimal("15.598365377"));
        List<RelevanceMenuItem> relevanceMenuItemsOrder01Expected = newArrayList(relevanceMenuItemPizzaCheeseOrder01Expected, relevanceMenuItemJapaneseOrder01Expected,
                relevanceMenuItemArabicEsfihasOrder01Expected, relevanceMenuItemArabicKibeOrder01Expected);

        RelevanceMenuItem relevanceMenuItemPizzaPortugueseOrder02Expected = new RelevanceMenuItem(itemPizzaPortugueseOrder02.getMenuUuid(), new BigDecimal("17.946063402"));
        RelevanceMenuItem relevanceMenuItemVeganOrder02Expected = new RelevanceMenuItem(itemVeganOrder02.getMenuUuid(), new BigDecimal("18.287923899"));
        RelevanceMenuItem relevanceMenuItemPizzaPepperoniOrder02Expected = new RelevanceMenuItem(itemPizzaPepperoniOrder02.getMenuUuid(), new BigDecimal("16.878989451"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder02Expected = new RelevanceMenuItem(itemArabicEsfihasOrder02.getMenuUuid(), new BigDecimal("20.851441406"));
        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder02Expected = new RelevanceMenuItem(itemPizzaCheeseOrder02.getMenuUuid(), new BigDecimal("15.739738822"));
        List<RelevanceMenuItem> relevanceMenuItemsOrder02Expected = newArrayList(relevanceMenuItemPizzaPortugueseOrder02Expected, relevanceMenuItemVeganOrder02Expected,
                relevanceMenuItemPizzaPepperoniOrder02Expected, relevanceMenuItemArabicEsfihasOrder02Expected, relevanceMenuItemPizzaCheeseOrder02Expected);

        RelevanceCategory relevanceCategoryPizzaOrder01Expected = new RelevanceCategory(Category.PIZZA, new BigDecimal("14.872457840"));
        RelevanceCategory relevanceCategoryJapaneseOrder01Expected = new RelevanceCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        RelevanceCategory relevanceCategoryArabicOrder01Expected = new RelevanceCategory(Category.ARABIC, new BigDecimal("42.013048183"));
        List<RelevanceCategory> relevanceCategoriesOrder01Expected = newArrayList(relevanceCategoryPizzaOrder01Expected, relevanceCategoryJapaneseOrder01Expected,
                relevanceCategoryArabicOrder01Expected);

        RelevanceCategory relevanceCategoryPizzaOrder02Expected = new RelevanceCategory(Category.PIZZA, new BigDecimal("50.636968354"));
        RelevanceCategory relevanceCategoryVeganOrder02Expected = new RelevanceCategory(Category.VEGAN, new BigDecimal("18.287923899"));
        RelevanceCategory relevanceCategoryArabicOrder02Expected = new RelevanceCategory(Category.ARABIC, new BigDecimal("20.851441406"));
        List<RelevanceCategory> relevanceCategoriesOrder02Expected = newArrayList(relevanceCategoryPizzaOrder02Expected, relevanceCategoryVeganOrder02Expected,
                relevanceCategoryArabicOrder02Expected);

        // when
        ordersExpected.forEach(o -> orderService.save(o).then().block());

        // then
        List<Order> ordersActual = orderRepository.findAllPresents().collectList().block();

        assertThat(ordersActual).hasSize(ordersExpected.size()).containsAll(ordersExpected);

        List<OrderRelevance> orderRelevances = orderRelevanceRepository.findAllPresents().collectList().block();

        assertThat(orderRelevances).hasSize(ordersExpected.size());
        assertThat(orderRelevances.stream().filter(o->o.getOrderUuid().equals(order01.getUuid())).findFirst().get().getRelevancesMenuItem())
                .hasSize(relevanceMenuItemsOrder01Expected.size()).containsAll(relevanceMenuItemsOrder01Expected);
        assertThat(orderRelevances.stream().filter(o->o.getOrderUuid().equals(order01.getUuid())).findFirst().get().getRelevancesCategory())
                .hasSize(relevanceCategoriesOrder01Expected.size()).containsAll(relevanceCategoriesOrder01Expected);
        assertThat(orderRelevances.stream().filter(o->o.getOrderUuid().equals(order02.getUuid())).findFirst().get().getRelevancesMenuItem())
                .hasSize(relevanceMenuItemsOrder02Expected.size()).containsAll(relevanceMenuItemsOrder02Expected);
        assertThat(orderRelevances.stream().filter(o->o.getOrderUuid().equals(order02.getUuid())).findFirst().get().getRelevancesCategory())
                .hasSize(relevanceCategoriesOrder02Expected.size()).containsAll(relevanceCategoriesOrder02Expected);
    }

    @Test
    public void testCancelOrdersAndRelevances() {
        Item itemPizzaCheeseOrder01 = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        Item itemJapaneseOrder01 = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        Item itemArabicEsfihasOrder01 = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemArabicKibeOrder01 = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());

        List<Item> itemsOrder01 = newArrayList(itemPizzaCheeseOrder01, itemJapaneseOrder01, itemArabicEsfihasOrder01, itemArabicKibeOrder01);

        Order order01 = generateTestOrder(itemsOrder01);

        Item itemPizzaPortugueseOrder02 = generateTestItem(1, menuPizzaPortuguese.getCategory(), menuPizzaPortuguese.getUuid(), menuPizzaPortuguese.getUnitPrice());
        Item itemVeganOrder02 = generateTestItem(3, menuVegan.getCategory(), menuVegan.getUuid(), menuVegan.getUnitPrice());
        Item itemPizzaPepperoniOrder02 = generateTestItem(1, menuPizzaPepperoni.getCategory(), menuPizzaPepperoni.getUuid(), menuPizzaPepperoni.getUnitPrice());
        Item itemArabicEsfihasOrder02 = generateTestItem(3, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemPizzaCheeseOrder02 = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());

        List<Item> itemsOrder02 = newArrayList(itemPizzaPortugueseOrder02, itemVeganOrder02, itemPizzaPepperoniOrder02,
                itemArabicEsfihasOrder02, itemPizzaCheeseOrder02);

        Order order02 = generateTestOrder(itemsOrder02);

        List<Order> orders = newArrayList(order01, order02);

        orders.forEach(o -> orderService.save(o).then().block());

        orderService.cancel(order01.getUuid()).block();

        List<Order> ordersActual = orderRepository.findAllPresents().collectList().block();

        assertThat(ordersActual).hasSize(1).contains(order02);

        List<OrderRelevance> orderRelevances = orderRelevanceRepository.findAllPresents().collectList().block();

        assertThat(orderRelevances).hasSize(1);
    }
}
