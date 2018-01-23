package ifood.score.service;

import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.infrastructure.service.order.Item;
import ifood.score.menu.Category;
import ifood.score.menu.Menu;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static ifood.score.support.GenerateTestData.generateTestItem;
import static ifood.score.support.GenerateTestData.generateTestMenu;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceCalculateRelevanceMenuItemTest {

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
        orderService.calculateRelevanceMenuItem(null, menuPizzaCheese.getUuid());
    }

    @Test
    public void testCalculateRelevanceWithNullMenuUuid() {
        List<Item> items = createDummyItens();

        BigDecimal relevance = orderService.calculateRelevanceMenuItem(items, null);

        assertThat(relevance).isNull();
    }

    private List<Item> createDummyItens() {
        Item itemPizza = generateTestItem(1, menuPizzaCheese.getCategory(), menuPizzaCheese.getUuid(), menuPizzaCheese.getUnitPrice());
        Item itemJapanese = generateTestItem(4, menuJapanese.getCategory(), menuJapanese.getUuid(), menuJapanese.getUnitPrice());
        Item itemArabicEsfihas = generateTestItem(4, menuArabicEsfihas.getCategory(), menuArabicEsfihas.getUuid(), menuArabicEsfihas.getUnitPrice());
        Item itemArabicKibe = generateTestItem(2, menuArabicKibe.getCategory(), menuArabicKibe.getUuid(), menuArabicKibe.getUnitPrice());

        return newArrayList(itemPizza, itemJapanese, itemArabicEsfihas, itemArabicKibe);
    }

    @Test
    public void testCalculateRelevanceWithMenuUuidNotExistsOnItensOrders() {
        List<Item> items = createDummyItens();

        BigDecimal relevance = orderService.calculateRelevanceMenuItem(items, UUID.randomUUID());

        assertThat(relevance).isNull();
    }

    @Test
    public void testCalculateRelevanceFromMenuItem() {
        List<Item> items = createDummyItens();

        BigDecimal relevance = orderService.calculateRelevanceMenuItem(items, menuPizzaCheese.getUuid());

        assertThat(relevance).isNotNull();
        assertThat(relevance).isEqualTo(new BigDecimal("14.872457840"));
    }
}
