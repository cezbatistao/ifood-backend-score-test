package ifood.score.service;

import com.google.common.base.VerifyException;
import ifood.score.domain.model.*;
import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static ifood.score.support.GenerateTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.array;
import static org.assertj.core.util.Lists.newArrayList;

@RunWith(MockitoJUnitRunner.class)
public class OrderRelevanceServiceTest {

    private OrderRelevanceService orderRelevanceService;

    private Menu menuPizzaCheese;
    private Menu menuPizzaPepperoni;
    private Menu menuPizzaPortuguese;
    private Menu menuJapanese;
    private Menu menuArabicEsfihas;
    private Menu menuArabicKibe;
    private Menu menuVegan;

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

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    private OrderRelevanceRepository orderRelevanceRepository;

    @Before
    public void setup() {
        orderRelevanceService = new OrderRelevanceService(orderRelevanceRepository);

        menuPizzaCheese = generateTestMenu(Category.PIZZA, new BigDecimal("20"));
        menuPizzaPepperoni = generateTestMenu(Category.PIZZA, new BigDecimal("23"));
        menuPizzaPortuguese = generateTestMenu(Category.PIZZA, new BigDecimal("26"));
        menuJapanese = generateTestMenu(Category.JAPANESE, new BigDecimal("8.9"));
        menuArabicEsfihas = generateTestMenu(Category.ARABIC, new BigDecimal("3.9"));
        menuArabicKibe = generateTestMenu(Category.ARABIC, new BigDecimal("5.5"));
        menuVegan = generateTestMenu(Category.VEGAN, new BigDecimal("3"));

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
        itemsOrder02 = newArrayList(itemPizzaPortugueseOrder02, itemVeganOrder02, itemPizzaPepperoniOrder02,
                itemArabicEsfihasOrder02, itemPizzaCheeseOrder02);
        order02 = generateTestOrder(itemsOrder02);
    }

    @Test
    public void testCalculateWithNullOrder() {
        expectedEx.expect(VerifyException.class);
        expectedEx.expectMessage("Order is required to calculateRelevance.");
        orderRelevanceService.calculateRelevance((Order) null);
    }

    @Test
    public void testCalculateWithEmptyOrder() {
        expectedEx.expect(VerifyException.class);
        expectedEx.expectMessage("Order is required to calculateRelevance.");
        orderRelevanceService.calculateRelevance(new Order());
    }

    @Test
    public void testCalculateWithOrder() {
        RelevanceMenuItem[] relevanceMenuItemsExpected = createDummyRelevanceMenuItensForOrder01(itemPizzaCheeseOrder01,
                itemJapaneseOrder01, itemArabicEsfihasOrder01, itemArabicKibeOrder01);
        RelevanceCategory[] relevanceCategoriesExpected = createDummyRelevanceCategoriesForOrder01();

        OrderRelevance orderRelevanceExpected = new OrderRelevance(order01.getUuid(), newArrayList(relevanceMenuItemsExpected),
                newArrayList(relevanceCategoriesExpected));

        OrderRelevance orderRelevanceActual = orderRelevanceService.calculateRelevance(order01);

        assertThat(orderRelevanceActual).isNotNull();
        assertThat(orderRelevanceActual.getOrderUuid()).isEqualTo(orderRelevanceExpected.getOrderUuid());
        assertThat(orderRelevanceActual.getRelevancesMenuItem())
                .hasSize(relevanceMenuItemsExpected.length).contains(relevanceMenuItemsExpected);
        assertThat(orderRelevanceActual.getRelevancesCategory())
                .hasSize(relevanceCategoriesExpected.length).contains(relevanceCategoriesExpected);
    }

    @Test
    public void testCalculateScoreOrders() {
        OrderRelevance orderRelevanceOrder01 = orderRelevanceService.calculateRelevance(order01);
        OrderRelevance orderRelevanceOrder02 = orderRelevanceService.calculateRelevance(order02);

        List<ScoreMenuItem> scoreMenuItemsExpected = createDummyScoreMenuItems();

        List<ScoreCategory> scoreCategoriesExpected = createDummyScoreCategories();

        Account accountActual = orderRelevanceService.calculateScore(orderRelevanceOrder01, orderRelevanceOrder02);
        List<ScoreMenuItem> scoreMenuItemsActual = accountActual.getScoreMenuItems();
        scoreMenuItemsActual.sort((s1, s2) -> s1.getMenuUuid().toString().compareTo(s2.getMenuUuid().toString()));
        List<ScoreCategory> scoreCategoriesActual = accountActual.getScoreCategories();
        scoreCategoriesActual.sort((s1, s2) -> s1.getCategory().compareTo(s2.getCategory()));

        assertThat(accountActual).isNotNull();
        assertThat(scoreMenuItemsActual)
                .isNotNull()
                .hasSize(scoreMenuItemsExpected.size())
                .containsAll(scoreMenuItemsExpected);
        assertThat(scoreCategoriesActual)
                .isNotNull()
                .hasSize(scoreCategoriesExpected.size())
                .containsAll(scoreCategoriesExpected);
    }

    private RelevanceMenuItem[] createDummyRelevanceMenuItensForOrder01(Item itemPizzaCheeseOrder01, Item itemJapaneseOrder01,
                                                                        Item itemArabicEsfihasOrder01, Item itemArabicKibeOrder01) {
        RelevanceMenuItem relevanceMenuItemPizzaCheeseOrder01Expected = new RelevanceMenuItem(itemPizzaCheeseOrder01.getMenuUuid(), new BigDecimal("14.872457840"));
        RelevanceMenuItem relevanceMenuItemJapaneseOrder01Expected = new RelevanceMenuItem(itemJapaneseOrder01.getMenuUuid(), new BigDecimal("39.684667263"));
        RelevanceMenuItem relevanceMenuItemArabicEsfihasOrder01Expected = new RelevanceMenuItem(itemArabicEsfihasOrder01.getMenuUuid(), new BigDecimal("26.269998228"));
        RelevanceMenuItem relevanceMenuItemArabicKibeOrder01Expected = new RelevanceMenuItem(itemArabicKibeOrder01.getMenuUuid(), new BigDecimal("15.598365377"));

        return array(relevanceMenuItemPizzaCheeseOrder01Expected, relevanceMenuItemJapaneseOrder01Expected, relevanceMenuItemArabicEsfihasOrder01Expected,
                relevanceMenuItemArabicKibeOrder01Expected);
    }

    private RelevanceCategory[] createDummyRelevanceCategoriesForOrder01() {
        RelevanceCategory relevanceCategoryPizzaOrder01Expected = new RelevanceCategory(Category.PIZZA, new BigDecimal("14.872457840"));
        RelevanceCategory relevanceCategoryJapaneseOrder01Expected = new RelevanceCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        RelevanceCategory relevanceCategoryArabicOrder01Expected = new RelevanceCategory(Category.ARABIC, new BigDecimal("42.013048183"));

        return array(relevanceCategoryPizzaOrder01Expected, relevanceCategoryJapaneseOrder01Expected, relevanceCategoryArabicOrder01Expected);
    }

    private List<ScoreCategory> createDummyScoreCategories() {
        ScoreCategory scoreCategoryPizzaExpected = new ScoreCategory(Category.PIZZA, new BigDecimal("32.754713097"));
        ScoreCategory scoreCategoryVeganExpected = new ScoreCategory(Category.VEGAN, new BigDecimal("18.287923899"));
        ScoreCategory scoreCategoryJapaneseExpected = new ScoreCategory(Category.JAPANESE, new BigDecimal("39.684667263"));
        ScoreCategory scoreCategoryArabicExpected = new ScoreCategory(Category.ARABIC, new BigDecimal("31.432244795"));

        List<ScoreCategory> scoreCategoriesExpected = newArrayList(scoreCategoryPizzaExpected, scoreCategoryVeganExpected,
                scoreCategoryJapaneseExpected, scoreCategoryArabicExpected);
        scoreCategoriesExpected.sort((s1, s2) -> s1.getCategory().compareTo(s2.getCategory()));
        return scoreCategoriesExpected;
    }

    private List<ScoreMenuItem> createDummyScoreMenuItems() {
        ScoreMenuItem scoreMenuItemPizzaCheeseExpected = new ScoreMenuItem(menuPizzaCheese.getUuid(), new BigDecimal("15.306098331"));
        ScoreMenuItem scoreMenuItemJapaneseExpected = new ScoreMenuItem(menuJapanese.getUuid(), new BigDecimal("39.684667263"));
        ScoreMenuItem scoreMenuItemArabicEsfihasExpected = new ScoreMenuItem(menuArabicEsfihas.getUuid(), new BigDecimal("23.560719817"));
        ScoreMenuItem scoreMenuItemArabicKibeExpected = new ScoreMenuItem(menuArabicKibe.getUuid(), new BigDecimal("15.598365377"));
        ScoreMenuItem scoreMenuItemPizzaPortugueseExpected = new ScoreMenuItem(menuPizzaPortuguese.getUuid(), new BigDecimal("17.946063402"));
        ScoreMenuItem scoreMenuItemVegaExpected = new ScoreMenuItem(menuVegan.getUuid(), new BigDecimal("18.287923899"));
        ScoreMenuItem scoreMenuItemPizzaPepperoniExpected = new ScoreMenuItem(menuPizzaPepperoni.getUuid(), new BigDecimal("16.878989451"));

        List<ScoreMenuItem> scoreMenuItemsExpected = newArrayList(scoreMenuItemPizzaCheeseExpected, scoreMenuItemJapaneseExpected,
                scoreMenuItemArabicEsfihasExpected, scoreMenuItemArabicKibeExpected, scoreMenuItemPizzaPortugueseExpected,
                scoreMenuItemVegaExpected, scoreMenuItemPizzaPepperoniExpected);
        scoreMenuItemsExpected.sort((s1, s2) -> s1.getMenuUuid().toString().compareTo(s2.getMenuUuid().toString()));
        return scoreMenuItemsExpected;
    }
}
