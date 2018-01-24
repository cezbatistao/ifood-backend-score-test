package ifood.score.domain.repository;

import ifood.score.domain.model.Score;
import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
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
import java.math.RoundingMode;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static ifood.score.support.GenerateTestData.generateTestMenu;
import static org.assertj.core.api.Assertions.assertThat;

public class ScoreRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;

    private ScoreMenuItem scoreMenuItemPizzaCheeseExpected;
    private ScoreMenuItem scoreMenuItemArabicKibeExpected;
    private ScoreMenuItem scoreMenuItemCokeExpected;
    private ScoreCategory scoreCategoryJapaneseExpected;
    private ScoreCategory scoreCategoryVeganExpected;

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

        scoreMenuItemPizzaCheeseExpected = new ScoreMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("15.306098331"));
        ScoreMenuItem scoreMenuItemJapaneseExpected = new ScoreMenuItem(menuJapanese.getUuid(), new BigDecimal("39.684667263"));
        ScoreMenuItem scoreMenuItemArabicEsfihasExpected = new ScoreMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("23.560719817"));
        scoreMenuItemArabicKibeExpected = new ScoreMenuItem(menuArabicKibe.getUuid(), new BigDecimal("15.598365377"));
        ScoreMenuItem scoreMenuItemPizzaPortugueseExpected = new ScoreMenuItem(menuPizzaPortuguese.getUuid(), new BigDecimal("58.973031701"));
        ScoreMenuItem scoreMenuItemVeganExpected = new ScoreMenuItem(menuVegan.getUuid(), new BigDecimal("18.287923899"));
        ScoreMenuItem scoreMenuItemPizzaPepperoniExpected = new ScoreMenuItem(menuPizzaPepperoni.getUuid(), new BigDecimal("16.878989451"));
        scoreMenuItemCokeExpected = new ScoreMenuItem(menuCoke.getUuid(), new BigDecimal("26.352313835"));
        ScoreMenuItem scoreMenuItemHamburgerExpected = new ScoreMenuItem(menuHamburger.getUuid(), new BigDecimal("65.616732283"));
        List<ScoreMenuItem> scoreMenuItensListExpected = newArrayList(scoreMenuItemPizzaCheeseExpected, scoreMenuItemJapaneseExpected, scoreMenuItemArabicEsfihasExpected,
                scoreMenuItemArabicKibeExpected, scoreMenuItemPizzaPortugueseExpected, scoreMenuItemVeganExpected, scoreMenuItemPizzaPepperoniExpected,
                scoreMenuItemCokeExpected, scoreMenuItemHamburgerExpected);
        scoreMenuItensListExpected.forEach(s-> scoreRepository.saveScoreMenuItem(s).block());

        ScoreCategory scoreCategoryPizzaExpected = new ScoreCategory(Category.PIZZA, new BigDecimal("55.169808731"));
        scoreCategoryVeganExpected = new ScoreCategory(Category.VEGAN, new BigDecimal("18.287923899"));
        scoreCategoryJapaneseExpected = new ScoreCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        ScoreCategory scoreCategoryArabicExpected = new ScoreCategory(Category.ARABIC, new BigDecimal("31.432244795"));
        ScoreCategory scoreCategoryHamburgerExpected = new ScoreCategory(Category.HAMBURGER, new BigDecimal("65.616732283"));
        ScoreCategory scoreCategoryOtherExpected = new ScoreCategory(Category.OTHER, new BigDecimal("26.352313835"));
        List<ScoreCategory> scoreCategoriesListExpected = newArrayList(scoreCategoryPizzaExpected, scoreCategoryVeganExpected, scoreCategoryJapaneseExpected,
                scoreCategoryArabicExpected, scoreCategoryHamburgerExpected, scoreCategoryOtherExpected);
        scoreCategoriesListExpected.forEach(s-> scoreRepository.saveCategory(s).block());
    }

    @Test
    public void testFindFirstScoreMenuItemAboveByScore() {
        double scoreToSearch = BigDecimal.valueOf(15.30).setScale(2, RoundingMode.HALF_UP).doubleValue();
        Score scoreMenuItemAboveAcutal = scoreRepository.findFirstScoreMenuItemAboveByScore(scoreToSearch).block();
        assertThat(scoreMenuItemAboveAcutal).isEqualTo(scoreMenuItemPizzaCheeseExpected);
    }

    @Test
    public void testFindFirstScoreMenuItemAboveByScoreFullValue() {
        double scoreToSearch = BigDecimal.valueOf(15.306098331).setScale(9, RoundingMode.HALF_UP).doubleValue();
        Score scoreMenuItemAboveAcutal = scoreRepository.findFirstScoreMenuItemAboveByScore(scoreToSearch).block();
        assertThat(scoreMenuItemAboveAcutal).isEqualTo(scoreMenuItemArabicKibeExpected);
    }

    @Test
    public void testFindFirstScoreMenuItemBelowByScore() {
        double scoreToSearch = BigDecimal.valueOf(39.68).setScale(2, RoundingMode.HALF_UP).doubleValue();
        Score scoreMenuItemAboveAcutal = scoreRepository.findFirstScoreMenuItemBelowByScore(scoreToSearch).block();
        assertThat(scoreMenuItemAboveAcutal).isEqualTo(scoreMenuItemCokeExpected);
    }

    @Test
    public void testFindFirstScoreCategoryAboveByScore() {
        double scoreToSearch = BigDecimal.valueOf(39.68).setScale(2, RoundingMode.HALF_UP).doubleValue();
        Score scoreMenuItemAboveAcutal = scoreRepository.findFirstScoreCategoryAboveByScore(scoreToSearch).block();
        assertThat(scoreMenuItemAboveAcutal).isEqualTo(scoreCategoryJapaneseExpected);
    }

    @Test
    public void testFindFirstScoreCategoryBelowByScore() {
        double scoreToSearch = BigDecimal.valueOf(22.28).setScale(2, RoundingMode.HALF_UP).doubleValue();
        Score scoreMenuItemAboveAcutal = scoreRepository.findFirstScoreCategoryBelowByScore(scoreToSearch).block();
        assertThat(scoreMenuItemAboveAcutal).isEqualTo(scoreCategoryVeganExpected);
    }
}
