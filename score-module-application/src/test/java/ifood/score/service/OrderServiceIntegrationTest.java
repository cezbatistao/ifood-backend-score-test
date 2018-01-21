package ifood.score.service;

import ifood.score.domain.model.OrderRelevance;
import ifood.score.domain.repository.entity.OrderMongo;
import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.domain.repository.OrderRepository;
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
    }

    @Test
    public void testSave() {
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

        ordersExpected.forEach(o -> orderService.save(o).then().block());

        List<Order> ordersActual = orderRepository.findAll().collectList().block();

        assertThat(ordersActual).hasSize(ordersExpected.size()).containsAll(ordersExpected);

        List<OrderRelevance> orderRelevances = orderRelevanceRepository.findAll().collectList().block();

        assertThat(orderRelevances).hasSize(ordersExpected.size());
    }
}
