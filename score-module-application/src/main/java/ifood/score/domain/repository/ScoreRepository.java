package ifood.score.domain.repository;

import ifood.score.domain.model.Score;
import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
import ifood.score.domain.repository.entity.ScoreCategoryMongo;
import ifood.score.domain.repository.entity.ScoreMenuItemMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ScoreRepository {

    private ReactiveMongoOperations operations;

    @Autowired
    public ScoreRepository(ReactiveMongoOperations operations) {
        this.operations = operations;
    }

    public Flux<ScoreMenuItem> saveAllScoreMenuItem(List<ScoreMenuItem> scoreMenuItems) {
        return Flux.fromIterable(scoreMenuItems).flatMap(s -> operations.save(mapperMenuItemMongo(s))).map(this::mapperMenuItem);
    }

    public Flux<ScoreCategory> saveAllCategory(List<ScoreCategory> scoreCategories) {
        return Flux.fromIterable(scoreCategories).flatMap(s -> operations.save(mapperCategoryMongo(s))).map(this::mapperCategory);
    }

    public Flux<ScoreMenuItem> findAllScoreMenuItem() {
        return operations.findAll(ScoreMenuItemMongo.class).map(this::mapperMenuItem);
    }

    public Flux<ScoreCategory> findAllScoreCategory() {
        return operations.findAll(ScoreCategoryMongo.class).map(this::mapperCategory);
    }

    public Mono<Score> findFirstScoreMenuItemAboveByScore(Double score) {
        Query query = query(where("score").gte(score));
        query.with(new Sort(Sort.Direction.ASC, "score"));
        return operations.findOne(query, ScoreMenuItemMongo.class).map(this::mapperMenuItem);
    }

    public Mono<Score> findFirstScoreMenuItemBelowByScore(Double score) {
        Query query = query(where("score").lte(score));
        query.with(new Sort(Sort.Direction.DESC, "score"));
        return operations.findOne(query, ScoreMenuItemMongo.class).map(this::mapperMenuItem);
    }

    public Mono<Score> findFirstScoreCategoryAboveByScore(Double score) {
        Query query = query(where("score").gte(score));
        query.with(new Sort(Sort.Direction.ASC, "score"));
        return operations.findOne(query, ScoreCategoryMongo.class).map(this::mapperCategory);
    }

    public Mono<Score> findFirstScoreCategoryBelowByScore(Double score) {
        Query query = query(where("score").lte(score));
        query.with(new Sort(Sort.Direction.DESC, "score"));
        return operations.findOne(query, ScoreCategoryMongo.class).map(this::mapperCategory);
    }

    private ScoreMenuItem mapperMenuItem(ScoreMenuItemMongo scoreMenuItemMongo) {
        return new ScoreMenuItem(scoreMenuItemMongo.getMenuUuid(), BigDecimal.valueOf(scoreMenuItemMongo.getScore()));
    }

    private ScoreMenuItemMongo mapperMenuItemMongo(ScoreMenuItem scoreMenuItem) {
        return new ScoreMenuItemMongo(scoreMenuItem.getMenuUuid(), scoreMenuItem.getScore().doubleValue());
    }

    private ScoreCategory mapperCategory(ScoreCategoryMongo scoreCategoryMongo) {
        return new ScoreCategory(scoreCategoryMongo.getCategory(), BigDecimal.valueOf(scoreCategoryMongo.getScore()));
    }

    private ScoreCategoryMongo mapperCategoryMongo(ScoreCategory scoreCategory) {
        return new ScoreCategoryMongo(scoreCategory.getCategory(), scoreCategory.getScore().doubleValue());
    }
}
