package ifood.score.service;

import ifood.score.domain.model.*;
import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.domain.repository.ScoreRepository;
import ifood.score.domain.repository.entity.OrderMongo;
import ifood.score.domain.repository.entity.OrderRelevanceMongo;
import ifood.score.domain.repository.entity.ScoreCategoryMongo;
import ifood.score.domain.repository.entity.ScoreMenuItemMongo;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;
import ifood.score.support.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static ifood.score.support.GenerateTestData.generateTestMenu;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderRelevanceServiceIntegrationTest extends AbstractIntegrationTest {

    private Menu menuPizzaCheese;
    private Menu menuPizzaPepperoni;
    private Menu menuPizzaPortuguese;
    private Menu menuJapanese;
    private Menu menuArabicEsfihas;
    private Menu menuArabicKibe;
    private Menu menuVegan;
    private Menu menuHamburger;
    private Menu menuCoke;

    private OrderRelevance order01Relevance;
    private OrderRelevance order02Relevance;
    private OrderRelevance order03Relevance;
    private OrderRelevance order04Relevance;

    @Autowired
    private OrderRelevanceService orderRelevanceService;

    @Autowired
    private OrderRelevanceRepository orderRelevanceRepository;

    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    private ScoreRepository scoreRepository;

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

        menuPizzaCheese = generateTestMenu(Category.PIZZA, new BigDecimal("20"));
        menuPizzaPepperoni = generateTestMenu(Category.PIZZA, new BigDecimal("23"));
        menuPizzaPortuguese = generateTestMenu(Category.PIZZA, new BigDecimal("26"));
        menuJapanese = generateTestMenu(Category.JAPANESE, new BigDecimal("8.9"));
        menuArabicEsfihas = generateTestMenu(Category.ARABIC, new BigDecimal("3.9"));
        menuArabicKibe = generateTestMenu(Category.ARABIC, new BigDecimal("5.5"));
        menuVegan = generateTestMenu(Category.VEGAN, new BigDecimal("3"));
        menuHamburger = generateTestMenu(Category.HAMBURGER, new BigDecimal("27.9"));
        menuCoke = generateTestMenu(Category.OTHER, new BigDecimal("4.5"));

        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder01 = new RelevanceMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("14.872457840"));
        RelevanceMenuItem relevanceMenuItemJapaneseOrder01 = new RelevanceMenuItem(menuJapanese.getUuid(), new BigDecimal("39.684667263"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder01 = new RelevanceMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("26.269998228"));
        RelevanceMenuItem relevanceMenuItemArabicKibeOrder01 = new RelevanceMenuItem(menuArabicKibe.getUuid(), new BigDecimal("15.598365377"));

        RelevanceCategory relevanceCategoryPizzaOrder01 = new RelevanceCategory(Category.PIZZA, new BigDecimal("14.872457840"));
        RelevanceCategory relevanceCategoryJapaneseOrder01 = new RelevanceCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        RelevanceCategory relevanceCategoryArabicOrder01 = new RelevanceCategory(Category.ARABIC, new BigDecimal("42.013048183"));

        order01Relevance = new OrderRelevance(
                UUID.randomUUID(),
                newArrayList(relevanceMenuItemPizzaCheeseOrder01, relevanceMenuItemJapaneseOrder01, relevanceMenuItemArabicEsfihasOrder01,
                        relevanceMenuItemArabicKibeOrder01),
                newArrayList(relevanceCategoryPizzaOrder01, relevanceCategoryJapaneseOrder01, relevanceCategoryArabicOrder01));
        orderRelevanceRepository.save(order01Relevance).block();

        RelevanceMenuItem relevanceMenuItemPizzaPortugueseOrder02 = new RelevanceMenuItem(menuPizzaPortuguese.getUuid(), new BigDecimal("17.946063402"));
        RelevanceMenuItem relevanceMenuItemVeganOrder02 = new RelevanceMenuItem(menuVegan.getUuid(), new BigDecimal("18.287923899"));
        RelevanceMenuItem relevanceMenuItemPizzaPepperoniOrder02 = new RelevanceMenuItem(menuPizzaPepperoni.getUuid(), new BigDecimal("16.878989451"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder02 = new RelevanceMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("20.851441406"));
        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder02 = new RelevanceMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("15.739738822"));

        RelevanceCategory relevanceCategoryPizzaOrder02 = new RelevanceCategory(Category.PIZZA, new BigDecimal("50.636968354"));
        RelevanceCategory relevanceCategoryVeganOrder02 = new RelevanceCategory(Category.VEGAN, new BigDecimal("18.287923899"));
        RelevanceCategory relevanceCategoryArabicOrder02 = new RelevanceCategory(Category.ARABIC, new BigDecimal("20.851441406"));

        order02Relevance = new OrderRelevance(
                UUID.randomUUID(),
                newArrayList(relevanceMenuItemPizzaPortugueseOrder02, relevanceMenuItemVeganOrder02, relevanceMenuItemPizzaPepperoniOrder02,
                        relevanceMenuItemArabicEsfihasOrder02, relevanceMenuItemPizzaCheeseOrder02),
                newArrayList(relevanceCategoryPizzaOrder02, relevanceCategoryVeganOrder02, relevanceCategoryArabicOrder02));
        orderRelevanceRepository.save(order02Relevance).block();

        RelevanceMenuItem relevanceMenuItemHamburgerGourmetOrder03 = new RelevanceMenuItem(menuHamburger.getUuid(), new BigDecimal("65.616732283"));
        RelevanceMenuItem relevanceMenuItemDietCokeOrder03 = new RelevanceMenuItem(menuCoke.getUuid(), new BigDecimal("26.352313835"));

        RelevanceCategory relevanceCategoryHamburgerOrder03 = new RelevanceCategory(Category.HAMBURGER, new BigDecimal("65.616732283"));
        RelevanceCategory relevanceCategoryOtherOrder03 = new RelevanceCategory(Category.OTHER, new BigDecimal("26.352313835"));

        order03Relevance = new OrderRelevance(
                UUID.randomUUID(),
                newArrayList(relevanceMenuItemHamburgerGourmetOrder03, relevanceMenuItemDietCokeOrder03),
                newArrayList(relevanceCategoryHamburgerOrder03, relevanceCategoryOtherOrder03));
        orderRelevanceRepository.save(order03Relevance).block();

        RelevanceMenuItem relevanceMenuItemPizzaPortugueseOrder04 = new RelevanceMenuItem(menuPizzaPortuguese.getUuid(), new BigDecimal("100.000000000"));

        RelevanceCategory relevanceCategoryPizzaOrder04 = new RelevanceCategory(Category.PIZZA, new BigDecimal("100.000000000"));

        order04Relevance = new OrderRelevance(
                UUID.randomUUID(),
                newArrayList(relevanceMenuItemPizzaPortugueseOrder04),
                newArrayList(relevanceCategoryPizzaOrder04));
        orderRelevanceRepository.save(order04Relevance).block();
    }

    @Test
    public void testCalculateScore() {
        ScoreMenuItem scoreMenuItemPizzaCheeseExpected = new ScoreMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("15.306098331"));
        ScoreMenuItem scoreMenuItemJapaneseExpected = new ScoreMenuItem(menuJapanese.getUuid(), new BigDecimal("39.684667263"));
        ScoreMenuItem scoreMenuItemArabicEsfihasExpected = new ScoreMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("23.560719817"));
        ScoreMenuItem scoreMenuItemArabicKibeExpected = new ScoreMenuItem(menuArabicKibe.getUuid(), new BigDecimal("15.598365377"));
        ScoreMenuItem scoreMenuItemPizzaPortugueseExpected = new ScoreMenuItem(menuPizzaPortuguese.getUuid(), new BigDecimal("58.973031701"));
        ScoreMenuItem scoreMenuItemVeganExpected = new ScoreMenuItem(menuVegan.getUuid(), new BigDecimal("18.287923899"));
        ScoreMenuItem scoreMenuItemPizzaPepperoniExpected = new ScoreMenuItem(menuPizzaPepperoni.getUuid(), new BigDecimal("16.878989451"));
        ScoreMenuItem scoreMenuItemCokeExpected = new ScoreMenuItem(menuCoke.getUuid(), new BigDecimal("26.352313835"));
        ScoreMenuItem scoreMenuItemHamburgerExpected = new ScoreMenuItem(menuHamburger.getUuid(), new BigDecimal("65.616732283"));
        List<ScoreMenuItem> scoreMenuItensListExpected = newArrayList(scoreMenuItemPizzaCheeseExpected, scoreMenuItemJapaneseExpected, scoreMenuItemArabicEsfihasExpected,
                scoreMenuItemArabicKibeExpected, scoreMenuItemPizzaPortugueseExpected, scoreMenuItemVeganExpected, scoreMenuItemPizzaPepperoniExpected,
                scoreMenuItemCokeExpected, scoreMenuItemHamburgerExpected);
        scoreMenuItensListExpected.sort((s1, s2) -> s1.getMenuUuid().compareTo(s2.getMenuUuid()));
        ScoreMenuItem[] scoreMenuItensExpected = scoreMenuItensListExpected.toArray(new ScoreMenuItem[scoreMenuItensListExpected.size()]);

        ScoreCategory scoreCategoryPizzaExpected = new ScoreCategory(Category.PIZZA, new BigDecimal("55.169808731"));
        ScoreCategory scoreCategoryVeganExpected = new ScoreCategory(Category.VEGAN, new BigDecimal("18.287923899"));
        ScoreCategory scoreCategoryJapaneseExpected = new ScoreCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        ScoreCategory scoreCategoryArabicExpected = new ScoreCategory(Category.ARABIC, new BigDecimal("31.432244795"));
        ScoreCategory scoreCategoryHamburgerExpected = new ScoreCategory(Category.HAMBURGER, new BigDecimal("65.616732283"));
        ScoreCategory scoreCategoryOtherExpected = new ScoreCategory(Category.OTHER, new BigDecimal("26.352313835"));
        List<ScoreCategory> scoreCategoriesListExpected = newArrayList(scoreCategoryPizzaExpected, scoreCategoryVeganExpected, scoreCategoryJapaneseExpected,
                scoreCategoryArabicExpected, scoreCategoryHamburgerExpected, scoreCategoryOtherExpected);
        scoreCategoriesListExpected.sort((s1, s2) -> s1.getCategory().compareTo(s2.getCategory()));
        ScoreCategory[] scoreCategoriesExpected = scoreCategoriesListExpected.toArray(new ScoreCategory[scoreCategoriesListExpected.size()]);

        orderRelevanceService.calculateScore().block();

        List<ScoreMenuItem> scoreMenuItensActual = scoreRepository.findAllScoreMenuItem().collectList().block();
        scoreMenuItensActual.sort((s1, s2) -> s1.getMenuUuid().compareTo(s2.getMenuUuid()));
        List<ScoreCategory> scoreCategoryActual = scoreRepository.findAllScoreCategory().collectList().block();
        scoreCategoryActual.sort((s1, s2) -> s1.getCategory().compareTo(s2.getCategory()));

        assertThat(scoreMenuItensActual).hasSize(scoreMenuItensExpected.length).contains(scoreMenuItensExpected);
        assertThat(scoreCategoryActual).hasSize(scoreCategoriesExpected.length).contains(scoreCategoriesExpected);
    }

    @Test
    public void testCalculateScoreOnlyOrdersActiveds() {
        orderRelevanceRepository.markCanceledByOrderUuid(order02Relevance.getOrderUuid()).block();
        orderRelevanceRepository.markExpiredByConfirmedByOrderUuid(order04Relevance.getOrderUuid()).block();

        ScoreMenuItem scoreMenuItemPizzaCheeseExpected = new ScoreMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("14.872457840"));
        ScoreMenuItem scoreMenuItemJapaneseExpected = new ScoreMenuItem(menuJapanese.getUuid(), new BigDecimal("39.684667263"));
        ScoreMenuItem scoreMenuItemArabicEsfihasExpected = new ScoreMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("26.269998228"));
        ScoreMenuItem scoreMenuItemArabicKibeExpected = new ScoreMenuItem(menuArabicKibe.getUuid(), new BigDecimal("15.598365377"));
        ScoreMenuItem scoreMenuItemCokeExpected = new ScoreMenuItem(menuCoke.getUuid(), new BigDecimal("26.352313835"));
        ScoreMenuItem scoreMenuItemHamburgerExpected = new ScoreMenuItem(menuHamburger.getUuid(), new BigDecimal("65.616732283"));
        List<ScoreMenuItem> scoreMenuItensListExpected = newArrayList(scoreMenuItemPizzaCheeseExpected, scoreMenuItemJapaneseExpected,
                scoreMenuItemArabicEsfihasExpected, scoreMenuItemArabicKibeExpected, scoreMenuItemCokeExpected, scoreMenuItemHamburgerExpected);
        scoreMenuItensListExpected.sort((s1, s2) -> s1.getMenuUuid().compareTo(s2.getMenuUuid()));
        ScoreMenuItem[] scoreMenuItensExpected = scoreMenuItensListExpected.toArray(new ScoreMenuItem[scoreMenuItensListExpected.size()]);

        ScoreCategory scoreCategoryPizzaExpected = new ScoreCategory(Category.PIZZA, new BigDecimal("14.872457840"));
        ScoreCategory scoreCategoryJapaneseExpected = new ScoreCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        ScoreCategory scoreCategoryArabicExpected = new ScoreCategory(Category.ARABIC, new BigDecimal("42.013048183"));
        ScoreCategory scoreCategoryHamburgerExpected = new ScoreCategory(Category.HAMBURGER, new BigDecimal("65.616732283"));
        ScoreCategory scoreCategoryOtherExpected = new ScoreCategory(Category.OTHER, new BigDecimal("26.352313835"));
        List<ScoreCategory> scoreCategoriesListExpected = newArrayList(scoreCategoryPizzaExpected, scoreCategoryJapaneseExpected,
                scoreCategoryArabicExpected, scoreCategoryHamburgerExpected, scoreCategoryOtherExpected);
        scoreCategoriesListExpected.sort((s1, s2) -> s1.getCategory().compareTo(s2.getCategory()));
        ScoreCategory[] scoreCategoriesExpected = scoreCategoriesListExpected.toArray(new ScoreCategory[scoreCategoriesListExpected.size()]);

        orderRelevanceService.calculateScore().block();

        List<ScoreMenuItem> scoreMenuItensActual = scoreRepository.findAllScoreMenuItem().collectList().block();
        scoreMenuItensActual.sort((s1, s2) -> s1.getMenuUuid().compareTo(s2.getMenuUuid()));
        List<ScoreCategory> scoreCategoryActual = scoreRepository.findAllScoreCategory().collectList().block();
        scoreCategoryActual.sort((s1, s2) -> s1.getCategory().compareTo(s2.getCategory()));

        assertThat(scoreMenuItensActual).hasSize(scoreMenuItensExpected.length).contains(scoreMenuItensExpected);
        assertThat(scoreCategoryActual).hasSize(scoreCategoriesExpected.length).contains(scoreCategoriesExpected);
    }

    @Test
    public void testCalculateScoreWithoutRelevanceValues() {
        orderRelevanceRepository.markCanceledByOrderUuid(order01Relevance.getOrderUuid()).block();
        orderRelevanceRepository.markCanceledByOrderUuid(order02Relevance.getOrderUuid()).block();
        orderRelevanceRepository.markExpiredByConfirmedByOrderUuid(order03Relevance.getOrderUuid()).block();
        orderRelevanceRepository.markExpiredByConfirmedByOrderUuid(order04Relevance.getOrderUuid()).block();

        orderRelevanceService.calculateScore().block();

        List<ScoreMenuItem> scoreMenuItensActual = scoreRepository.findAllScoreMenuItem().collectList().block();
        List<ScoreCategory> scoreCategoryActual = scoreRepository.findAllScoreCategory().collectList().block();

        assertThat(scoreMenuItensActual).isEmpty();
        assertThat(scoreCategoryActual).isEmpty();
    }
}
