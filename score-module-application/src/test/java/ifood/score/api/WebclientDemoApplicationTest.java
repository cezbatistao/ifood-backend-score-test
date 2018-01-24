package ifood.score.api;

import com.google.common.collect.Lists;
import ifood.score.domain.model.*;
import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.domain.repository.OrderRepository;
import ifood.score.domain.repository.ScoreRepository;
import ifood.score.domain.repository.entity.OrderMongo;
import ifood.score.domain.repository.entity.OrderRelevanceMongo;
import ifood.score.domain.repository.entity.ScoreCategoryMongo;
import ifood.score.domain.repository.entity.ScoreMenuItemMongo;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;
import ifood.score.support.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static ifood.score.support.GenerateTestData.*;
import static java.lang.String.format;
import static org.assertj.core.util.Lists.newArrayList;

public class WebclientDemoApplicationTest extends AbstractIntegrationTest {

    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderRelevanceRepository orderRelevanceRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private WebTestClient webTestClient;

    private ScoreMenuItem scoreMenuItemPizzaCheeseExpected;
    private ScoreMenuItem scoreMenuItemCokeExpected;
    private ScoreCategory scoreCategoryJapaneseExpected;
    private ScoreCategory scoreCategoryVeganExpected;

    private Order order02;

    @Before
    public void setup() {
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

        reactiveMongoOperations.collectionExists(ScoreMenuItemMongo.class)
                .flatMap(exists -> exists ? reactiveMongoOperations.dropCollection(ScoreMenuItemMongo.class) : Mono.just(exists))
                .flatMap(o -> reactiveMongoOperations.createCollection(ScoreMenuItemMongo.class, CollectionOptions.empty()))
                .then()
                .block();

        reactiveMongoOperations.collectionExists(ScoreCategoryMongo.class)
                .flatMap(exists -> exists ? reactiveMongoOperations.dropCollection(ScoreCategoryMongo.class) : Mono.just(exists))
                .flatMap(o -> reactiveMongoOperations.createCollection(ScoreCategoryMongo.class, CollectionOptions.empty()))
                .then()
                .block();

        Menu menuPizzaCheese = generateTestMenu(Category.PIZZA, new BigDecimal("20"));
        Menu menuPizzaPepperoni = generateTestMenu(Category.PIZZA, new BigDecimal("23"));
        Menu menuPizzaPortuguese = generateTestMenu(Category.PIZZA, new BigDecimal("26"));
        Menu menuJapanese = generateTestMenu(Category.JAPANESE, new BigDecimal("8.9"));
        Menu menuArabicEsfihas = generateTestMenu(Category.ARABIC, new BigDecimal("3.9"));
        Menu menuArabicKibe = generateTestMenu(Category.ARABIC, new BigDecimal("5.5"));
        Menu menuVegan = generateTestMenu(Category.VEGAN, new BigDecimal("3"));
        Menu menuHamburger = generateTestMenu(Category.HAMBURGER, new BigDecimal("27.9"));
        Menu menuCoke = generateTestMenu(Category.OTHER, new BigDecimal("4.5"));

        Item itemPizzaCheeseOrder01 = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        Item itemJapaneseOrder01 = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        Item itemArabicEsfihasOrder01 = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemArabicKibeOrder01 = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());
        List<Item> itemsOrder01 = newArrayList(itemPizzaCheeseOrder01, itemJapaneseOrder01, itemArabicEsfihasOrder01, itemArabicKibeOrder01);
        Order order01 = generateTestOrder(itemsOrder01);
        orderRepository.save(order01).block();

        Item itemPizzaPortugueseOrder02 = generateTestItem(1, menuPizzaPortuguese.getCategory(), menuPizzaPortuguese.getUuid(), menuPizzaPortuguese.getUnitPrice());
        Item itemVeganOrder02 = generateTestItem(3, menuVegan.getCategory(), menuVegan.getUuid(), menuVegan.getUnitPrice());
        Item itemPizzaPepperoniOrder02 = generateTestItem(1, menuPizzaPepperoni.getCategory(), menuPizzaPepperoni.getUuid(), menuPizzaPepperoni.getUnitPrice());
        Item itemArabicEsfihasOrder02 = generateTestItem(3, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemPizzaCheeseOrder02 = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        List<Item> itemsOrder02 = newArrayList(itemPizzaPortugueseOrder02, itemVeganOrder02, itemPizzaPepperoniOrder02, itemArabicEsfihasOrder02, itemPizzaCheeseOrder02);
        order02 = generateTestOrder(itemsOrder02);
        orderRepository.save(order02).block();

        Item itemHamburgerGourmetOrder03 = generateTestItem(1, menuHamburger.getCategory(), menuHamburger.getUuid(), menuHamburger.getUnitPrice());
        Item itemDietCokeOrder03 = generateTestItem(1, menuCoke.getCategory(), menuCoke.getUuid(), menuCoke.getUnitPrice());
        List<Item> itemsOrder03 = newArrayList(itemHamburgerGourmetOrder03, itemDietCokeOrder03);
        Order order03 = generateTestOrder(itemsOrder03);
        orderRepository.save(order03).block();

        Item itemMenuItemPizzaPortugueseOrder04 = generateTestItem(1, menuPizzaPortuguese.getCategory(), menuPizzaPortuguese.getUuid(), menuPizzaPortuguese.getUnitPrice());
        List<Item> itemsOrder04 = newArrayList(itemMenuItemPizzaPortugueseOrder04);
        Order order04 = generateTestOrder(itemsOrder04);
        orderRepository.save(order04).block();

        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder01 = new RelevanceMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("14.872457840"));
        RelevanceMenuItem relevanceMenuItemJapaneseOrder01 = new RelevanceMenuItem(menuJapanese.getUuid(), new BigDecimal("39.684667263"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder01 = new RelevanceMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("26.269998228"));
        RelevanceMenuItem relevanceMenuItemArabicKibeOrder01 = new RelevanceMenuItem(menuArabicKibe.getUuid(), new BigDecimal("15.598365377"));

        RelevanceCategory relevanceCategoryPizzaOrder01 = new RelevanceCategory(Category.PIZZA, new BigDecimal("14.872457840"));
        RelevanceCategory relevanceCategoryJapaneseOrder01 = new RelevanceCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        RelevanceCategory relevanceCategoryArabicOrder01 = new RelevanceCategory(Category.ARABIC, new BigDecimal("42.013048183"));

        OrderRelevance order01Relevance = new OrderRelevance(
                UUID.randomUUID(),
                Lists.newArrayList(relevanceMenuItemPizzaCheeseOrder01, relevanceMenuItemJapaneseOrder01, relevanceMenuItemArabicEsfihasOrder01,
                        relevanceMenuItemArabicKibeOrder01),
                Lists.newArrayList(relevanceCategoryPizzaOrder01, relevanceCategoryJapaneseOrder01, relevanceCategoryArabicOrder01));
        orderRelevanceRepository.save(order01Relevance).block();

        RelevanceMenuItem relevanceMenuItemPizzaPortugueseOrder02 = new RelevanceMenuItem(menuPizzaPortuguese.getUuid(), new BigDecimal("17.946063402"));
        RelevanceMenuItem relevanceMenuItemVeganOrder02 = new RelevanceMenuItem(menuVegan.getUuid(), new BigDecimal("18.287923899"));
        RelevanceMenuItem relevanceMenuItemPizzaPepperoniOrder02 = new RelevanceMenuItem(menuPizzaPepperoni.getUuid(), new BigDecimal("16.878989451"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder02 = new RelevanceMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("20.851441406"));
        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder02 = new RelevanceMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("15.739738822"));

        RelevanceCategory relevanceCategoryPizzaOrder02 = new RelevanceCategory(Category.PIZZA, new BigDecimal("50.636968354"));
        RelevanceCategory relevanceCategoryVeganOrder02 = new RelevanceCategory(Category.VEGAN, new BigDecimal("18.287923899"));
        RelevanceCategory relevanceCategoryArabicOrder02 = new RelevanceCategory(Category.ARABIC, new BigDecimal("20.851441406"));

        OrderRelevance order02Relevance = new OrderRelevance(
                UUID.randomUUID(),
                Lists.newArrayList(relevanceMenuItemPizzaPortugueseOrder02, relevanceMenuItemVeganOrder02, relevanceMenuItemPizzaPepperoniOrder02,
                        relevanceMenuItemArabicEsfihasOrder02, relevanceMenuItemPizzaCheeseOrder02),
                Lists.newArrayList(relevanceCategoryPizzaOrder02, relevanceCategoryVeganOrder02, relevanceCategoryArabicOrder02));
        orderRelevanceRepository.save(order02Relevance).block();

        RelevanceMenuItem relevanceMenuItemHamburgerGourmetOrder03 = new RelevanceMenuItem(menuHamburger.getUuid(), new BigDecimal("65.616732283"));
        RelevanceMenuItem relevanceMenuItemDietCokeOrder03 = new RelevanceMenuItem(menuCoke.getUuid(), new BigDecimal("26.352313835"));

        RelevanceCategory relevanceCategoryHamburgerOrder03 = new RelevanceCategory(Category.HAMBURGER, new BigDecimal("65.616732283"));
        RelevanceCategory relevanceCategoryOtherOrder03 = new RelevanceCategory(Category.OTHER, new BigDecimal("26.352313835"));

        OrderRelevance order03Relevance = new OrderRelevance(
                UUID.randomUUID(),
                Lists.newArrayList(relevanceMenuItemHamburgerGourmetOrder03, relevanceMenuItemDietCokeOrder03),
                Lists.newArrayList(relevanceCategoryHamburgerOrder03, relevanceCategoryOtherOrder03));
        orderRelevanceRepository.save(order03Relevance).block();

        RelevanceMenuItem relevanceMenuItemPizzaPortugueseOrder04 = new RelevanceMenuItem(menuPizzaPortuguese.getUuid(), new BigDecimal("100.000000000"));

        RelevanceCategory relevanceCategoryPizzaOrder04 = new RelevanceCategory(Category.PIZZA, new BigDecimal("100.000000000"));

        OrderRelevance order04Relevance = new OrderRelevance(
                UUID.randomUUID(),
                Lists.newArrayList(relevanceMenuItemPizzaPortugueseOrder04),
                Lists.newArrayList(relevanceCategoryPizzaOrder04));
        orderRelevanceRepository.save(order04Relevance).block();

        scoreMenuItemPizzaCheeseExpected = new ScoreMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("15.306098331"));
        ScoreMenuItem scoreMenuItemJapaneseExpected = new ScoreMenuItem(menuJapanese.getUuid(), new BigDecimal("39.684667263"));
        ScoreMenuItem scoreMenuItemArabicEsfihasExpected = new ScoreMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("23.560719817"));
        ScoreMenuItem scoreMenuItemArabicKibeExpected = new ScoreMenuItem(menuArabicKibe.getUuid(), new BigDecimal("15.598365377"));
        ScoreMenuItem scoreMenuItemPizzaPortugueseExpected = new ScoreMenuItem(menuPizzaPortuguese.getUuid(), new BigDecimal("58.973031701"));
        ScoreMenuItem scoreMenuItemVeganExpected = new ScoreMenuItem(menuVegan.getUuid(), new BigDecimal("18.287923899"));
        ScoreMenuItem scoreMenuItemPizzaPepperoniExpected = new ScoreMenuItem(menuPizzaPepperoni.getUuid(), new BigDecimal("16.878989451"));
        scoreMenuItemCokeExpected = new ScoreMenuItem(menuCoke.getUuid(), new BigDecimal("26.352313835"));
        ScoreMenuItem scoreMenuItemHamburgerExpected = new ScoreMenuItem(menuHamburger.getUuid(), new BigDecimal("65.616732283"));
        List<ScoreMenuItem> scoreMenuItensListExpected = Lists.newArrayList(scoreMenuItemPizzaCheeseExpected, scoreMenuItemJapaneseExpected, scoreMenuItemArabicEsfihasExpected,
                scoreMenuItemArabicKibeExpected, scoreMenuItemPizzaPortugueseExpected, scoreMenuItemVeganExpected, scoreMenuItemPizzaPepperoniExpected,
                scoreMenuItemCokeExpected, scoreMenuItemHamburgerExpected);
        scoreMenuItensListExpected.forEach(s-> scoreRepository.saveScoreMenuItem(s).block());

        ScoreCategory scoreCategoryPizzaExpected = new ScoreCategory(Category.PIZZA, new BigDecimal("55.169808731"));
        scoreCategoryVeganExpected = new ScoreCategory(Category.VEGAN, new BigDecimal("18.287923899"));
        scoreCategoryJapaneseExpected = new ScoreCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        ScoreCategory scoreCategoryArabicExpected = new ScoreCategory(Category.ARABIC, new BigDecimal("31.432244795"));
        ScoreCategory scoreCategoryHamburgerExpected = new ScoreCategory(Category.HAMBURGER, new BigDecimal("65.616732283"));
        ScoreCategory scoreCategoryOtherExpected = new ScoreCategory(Category.OTHER, new BigDecimal("26.352313835"));
        List<ScoreCategory> scoreCategoriesListExpected = Lists.newArrayList(scoreCategoryPizzaExpected, scoreCategoryVeganExpected, scoreCategoryJapaneseExpected,
                scoreCategoryArabicExpected, scoreCategoryHamburgerExpected, scoreCategoryOtherExpected);
        scoreCategoriesListExpected.forEach(s-> scoreRepository.saveCategory(s).block());
    }

    @Test
    public void testEchoPostRequest() {
        webTestClient.post().uri("/echo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just("Hello Score!"), String.class)
                .exchange()
                .expectStatus().isOk().expectBody().equals("Hello Score!");
    }

    @Test
    public void testGetOrderByOrderUuid() {
        webTestClient.get().uri(format("/orders/%s", order02.getUuid().toString()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(Order.class);
    }

    @Test
    public void testGetOrderRelevanceByOrderUuid() {
        webTestClient.get().uri(format("/orders/%s/relevances", order02.getUuid().toString()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(OrderRelevance.class);
    }

    @Test
    public void testGetAboveScoreMenuItemByValueScore() {
        webTestClient.get().uri(format("/menu-item/score/%s/above", "15.30"))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("menuUuid").isEqualTo(scoreMenuItemPizzaCheeseExpected.getMenuUuid().toString())
                .jsonPath("score").isEqualTo(scoreMenuItemPizzaCheeseExpected.getScore().toString());
    }

    @Test
    public void testGetBelowScoreMenuItemByValueScore() {
        webTestClient.get().uri(format("/menu-item/score/%s/below", "39.68"))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("menuUuid").isEqualTo(scoreMenuItemCokeExpected.getMenuUuid().toString())
                .jsonPath("score").isEqualTo(scoreMenuItemCokeExpected.getScore().toString());
    }

    @Test
    public void testGetAboveScoreCategoryByValueScore() {
        webTestClient.get().uri(format("/category/score/%s/above", "39.68"))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("category").isEqualTo(scoreCategoryJapaneseExpected.getCategory().toString())
                .jsonPath("score").isEqualTo(scoreCategoryJapaneseExpected.getScore().toString());
    }

    @Test
    public void testGetBelowScoreCategoryByValueScore() {
        webTestClient.get().uri(format("/category/score/%s/below", "22.28"))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("category").isEqualTo(scoreCategoryVeganExpected.getCategory().toString())
                .jsonPath("score").isEqualTo(scoreCategoryVeganExpected.getScore().toString());
    }
}
