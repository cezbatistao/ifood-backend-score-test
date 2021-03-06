package ifood.score.service;

import ifood.score.domain.model.Score;
import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
import ifood.score.domain.repository.ScoreRepository;
import ifood.score.service.score.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ScoreService {

    private ScoreRepository scoreRepository;

    @Autowired
    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public Mono<ScoreMenuItem> findFirstScoreMenuItemByScoreAndType(Double score, Type type) {
        Mono<Score> findScore;

        if (type == Type.ABOVE) {
            findScore = scoreRepository.findFirstScoreMenuItemAboveByScore(score);
        } else {
            findScore = scoreRepository.findFirstScoreMenuItemBelowByScore(score);
        }

        return findScore.map(sm -> (ScoreMenuItem) sm);
    }

    public Mono<ScoreCategory> findFirstScoreCategoryByScoreAndType(Double score, Type type) {
        Mono<Score> findScore;

        if (type == Type.ABOVE) {
            findScore = scoreRepository.findFirstScoreCategoryAboveByScore(score);
        } else {
            findScore = scoreRepository.findFirstScoreCategoryBelowByScore(score);
        }

        return findScore.map(sm -> (ScoreCategory) sm);
    }
}
