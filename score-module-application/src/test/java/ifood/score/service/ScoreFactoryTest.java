package ifood.score.service;

import ifood.score.domain.model.Score;
import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
import ifood.score.menu.Category;
import ifood.score.service.ScoreFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ScoreFactoryTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testScoreFactoryWithUUIDKey() {
        UUID key = UUID.randomUUID();
        BigDecimal scoreValue = new BigDecimal("10.0");
        ScoreMenuItem scoreMenuItemExptected = new ScoreMenuItem(key, scoreValue);

        Score score = ScoreFactory.getScore(key, scoreValue);

        assertThat(score).isInstanceOf(ScoreMenuItem.class).isEqualTo(scoreMenuItemExptected);
    }

    @Test
    public void testScoreFactoryWithCategoryKey() {
        Category key = Category.PIZZA;
        BigDecimal scoreValue = new BigDecimal("10.0");
        ScoreCategory scoreCategoryExptected = new ScoreCategory(key, scoreValue);

        Score score = ScoreFactory.getScore(key, scoreValue);

        assertThat(score).isInstanceOf(ScoreCategory.class).isEqualTo(scoreCategoryExptected);
    }

    @Test
    public void testScoreFactoryWithUnkownKey() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Score can't created with key [1] of type [java.lang.Long]");
        ScoreFactory.getScore(1L, new BigDecimal("10.0"));
    }
}
