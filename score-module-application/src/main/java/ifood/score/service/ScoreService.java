package ifood.score.service;

import ifood.score.domain.model.Score;
import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
import ifood.score.domain.repository.ScoreRepository;
import ifood.score.service.score.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static ifood.score.service.score.Type.ABOVE;

@Service
public class ScoreService {

    private ScoreRepository scoreRepository;

    @Autowired
    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public Mono<Score> findFirstScoreMenuItemByScoreAndType(Double score, Type type) {
        Mono<Score> findScore = Mono.empty();
        switch (type) {
            case ABOVE:
                findScore = scoreRepository.findFirstScoreMenuItemAboveByScore(score);
                break;
            case BELOW:
                findScore = scoreRepository.findFirstScoreMenuItemBelowByScore(score);
                break;
        }

        return findScore;
    }

    public Mono<Score> findFirstScoreCategoryByScoreAndType(Double score, Type type) {
        Mono<Score> findScore = Mono.empty();
        switch (type) {
            case ABOVE:
                findScore = scoreRepository.findFirstScoreCategoryAboveByScore(score);
                break;
            case BELOW:
                findScore = scoreRepository.findFirstScoreCategoryBelowByScore(score);
                break;
        }

        return findScore;
    }
}
