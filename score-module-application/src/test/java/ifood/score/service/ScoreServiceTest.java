package ifood.score.service;

import ifood.score.domain.repository.ScoreRepository;
import ifood.score.service.score.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScoreServiceTest {

    private ScoreService scoreService;

    @Mock
    private ScoreRepository scoreRepository;

    @Before
    public void setup() {
        scoreService = new ScoreService(scoreRepository);
    }

    @Test
    public void verifyIfFindMenuItemAboveIsCalled() {
        double score = 10.0;
        Type above = Type.ABOVE;

        scoreService.findFirstScoreMenuItemByScoreAndType(score, above);

        verify(scoreRepository, times(1)).findFirstScoreMenuItemAboveByScore(score);
    }

    @Test
    public void verifyIfFindMenuItemBelowIsCalled() {
        double score = 10.0;
        Type below = Type.BELOW;

        scoreService.findFirstScoreMenuItemByScoreAndType(score, below);

        verify(scoreRepository, times(1)).findFirstScoreMenuItemBelowByScore(score);
    }

    @Test
    public void verifyIfFindCategoryAboveIsCalled() {
        double score = 10.0;
        Type above = Type.ABOVE;

        scoreService.findFirstScoreCategoryByScoreAndType(score, above);

        verify(scoreRepository, times(1)).findFirstScoreCategoryAboveByScore(score);
    }

    @Test
    public void verifyIfFindCategoryBelowIsCalled() {
        double score = 10.0;
        Type below = Type.BELOW;

        scoreService.findFirstScoreCategoryByScoreAndType(score, below);

        verify(scoreRepository, times(1)).findFirstScoreCategoryBelowByScore(score);
    }
}
