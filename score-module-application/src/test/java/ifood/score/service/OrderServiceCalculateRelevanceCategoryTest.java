package ifood.score.service;

import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.infrastructure.service.order.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static ifood.score.support.GenerateTestData.generateTestItem;
import static ifood.score.support.GenerateTestData.generateTestMenu;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceCalculateRelevanceCategoryTest {

    private OrderRelevanceService orderService;

    private Menu menuPizzaCheese;
    private Menu menuJapanese;
    private Menu menuArabicEsfihas;
    private Menu menuArabicKibe;

    @Mock
    private OrderRelevanceRepository orderRelevanceRepository;

    @Before
    public void setup() {
        orderService = new OrderRelevanceService(orderRelevanceRepository);

        menuPizzaCheese = generateTestMenu(Category.PIZZA, new BigDecimal("20"));
        menuJapanese = generateTestMenu(Category.JAPANESE, new BigDecimal("8.9"));
        menuArabicEsfihas = generateTestMenu(Category.ARABIC, new BigDecimal("3.9"));
        menuArabicKibe = generateTestMenu(Category.ARABIC, new BigDecimal("5.5"));
    }

    @Test(expected = NullPointerException.class)
    public void testCalculateRelevanceWithNullOrder() {
        orderService.calculateRelevanceCategory(null, Category.BRAZILIAN);
    }

    @Test
    public void testCalculateRelevanceCategoryWithCategoryNotExistsOnItensOrders() {
        Item itemPizza = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        Item itemJapanese = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        Item itemArabicEsfihas = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemArabicKibe = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());

        List<Item> items = newArrayList(itemPizza, itemJapanese, itemArabicEsfihas, itemArabicKibe);

        BigDecimal relevance = orderService.calculateRelevanceCategory(items, Category.BRAZILIAN);

        assertThat(relevance).isNull();
    }

    @Test
    public void testCalculateRelevanceCategoryWithNullCategory() {
        Item itemPizza = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        Item itemJapanese = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        Item itemArabicEsfihas = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemArabicKibe = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());

        List<Item> items = newArrayList(itemPizza, itemJapanese, itemArabicEsfihas, itemArabicKibe);

        BigDecimal relevance = orderService.calculateRelevanceCategory(items, null);

        assertThat(relevance).isNull();
    }

    @Test
    public void testCalculateRelevanceCategoryWithOneItem() {
        Item itemPizza = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        Item itemJapanese = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        Item itemArabicEsfihas = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemArabicKibe = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());

        List<Item> items = newArrayList(itemPizza, itemJapanese, itemArabicEsfihas, itemArabicKibe);

        BigDecimal relevance = orderService.calculateRelevanceCategory(items, Category.PIZZA);

        assertThat(relevance).isNotNull();
        assertThat(relevance).isEqualTo(new BigDecimal("14.872457840"));
    }

    @Test
    public void testCalculateRelevanceCategoryWithManyItem() {
        Item itemPizza = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        Item itemJapanese = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        Item itemArabicEsfihas = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemArabicKibe = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());

        List<Item> items = newArrayList(itemPizza, itemJapanese, itemArabicEsfihas, itemArabicKibe);

        BigDecimal relevance = orderService.calculateRelevanceCategory(items, Category.ARABIC);

        assertThat(relevance).isNotNull();
        assertThat(relevance).isEqualTo(new BigDecimal("42.013048183"));
    }
}
