package ifood.score.domain.repository;

import ifood.score.domain.model.Score;
import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
import ifood.score.domain.repository.entity.OrderRelevanceMongo;
import ifood.score.domain.repository.entity.ScoreCategoryMongo;
import ifood.score.domain.repository.entity.ScoreMenuItemMongo;
import ifood.score.domain.repository.entity.StatusOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ScoreRepository {

    private static final String DOCUMENT_COLUMN_SCORE = "score";
    private static final String DOCUMENT_COLUMN_STATUS = "status";

    private ReactiveMongoOperations operations;

    @Autowired
    public ScoreRepository(ReactiveMongoOperations operations) {
        this.operations = operations;
    }

    public Mono<ScoreMenuItem> saveScoreMenuItem(ScoreMenuItem scoreMenuItem) {
        return this.saveScoreMenuItem(this.mapperMenuItem(scoreMenuItem)).map(this::mapperMenuItem);
    }

    public Mono<ScoreCategory> saveCategory(ScoreCategory scoreCategory) {
        return this.saveCategoryItem(this.mapperCategory(scoreCategory)).map(this::mapperCategory);
    }

    public Flux<ScoreMenuItem> aggregateAvgMenuItemUuidByStatusActive() {
        MatchOperation match = match(where(DOCUMENT_COLUMN_STATUS).is(StatusOrder.ACTIVE));
        UnwindOperation unwindRelevanceMenuItem = unwind("relevancesMenuItem");
        GroupOperation avgRelevanceMenuItem = group("relevancesMenuItem.menuUuid").avg("relevancesMenuItem.relevance").as(DOCUMENT_COLUMN_SCORE);

        return operations
                .aggregate(
                        newAggregation(
                                match,
                                unwindRelevanceMenuItem,
                                avgRelevanceMenuItem),
                        OrderRelevanceMongo.class,
                        ScoreMenuItemMongo.class).flatMap(this::saveScoreMenuItem).map(this::mapperMenuItem);
    }

    public Flux<ScoreCategory> aggregateAvgCategoryUuidByStatusActive() {
        MatchOperation match = match(where(DOCUMENT_COLUMN_STATUS).is(StatusOrder.ACTIVE));
        UnwindOperation unwindRelevanceCategory = unwind("relevancesCategory");
        GroupOperation avgRelevanceCategory = group("relevancesCategory.category").avg("relevancesCategory.relevance").as(DOCUMENT_COLUMN_SCORE);

        return operations
                .aggregate(
                        newAggregation(
                                match,
                                unwindRelevanceCategory,
                                avgRelevanceCategory),
                        OrderRelevanceMongo.class,
                        ScoreCategoryMongo.class).flatMap(this::saveCategoryItem).map(this::mapperCategory);
    }

    public Flux<ScoreMenuItem> findAllScoreMenuItem() {
        return operations.findAll(ScoreMenuItemMongo.class).map(this::mapperMenuItem);
    }

    public Flux<ScoreCategory> findAllScoreCategory() {
        return operations.findAll(ScoreCategoryMongo.class).map(this::mapperCategory);
    }

    public Mono<Score> findFirstScoreMenuItemAboveByScore(Double score) {
        Query query = query(where(DOCUMENT_COLUMN_SCORE).gt(score));
        query.with(new Sort(Sort.Direction.ASC, DOCUMENT_COLUMN_SCORE));
        return operations.findOne(query, ScoreMenuItemMongo.class).map(this::mapperMenuItem);
    }

    public Mono<Score> findFirstScoreMenuItemBelowByScore(Double score) {
        Query query = query(where(DOCUMENT_COLUMN_SCORE).lt(score));
        query.with(new Sort(Sort.Direction.DESC, DOCUMENT_COLUMN_SCORE));
        return operations.findOne(query, ScoreMenuItemMongo.class).map(this::mapperMenuItem);
    }

    public Mono<Score> findFirstScoreCategoryAboveByScore(Double score) {
        Query query = query(where(DOCUMENT_COLUMN_SCORE).gt(score));
        query.with(new Sort(Sort.Direction.ASC, DOCUMENT_COLUMN_SCORE));
        return operations.findOne(query, ScoreCategoryMongo.class).map(this::mapperCategory);
    }

    public Mono<Score> findFirstScoreCategoryBelowByScore(Double score) {
        Query query = query(where(DOCUMENT_COLUMN_SCORE).lt(score));
        query.with(new Sort(Sort.Direction.DESC, DOCUMENT_COLUMN_SCORE));
        return operations.findOne(query, ScoreCategoryMongo.class).map(this::mapperCategory);
    }

    private Mono<ScoreMenuItemMongo> saveScoreMenuItem(ScoreMenuItemMongo scoreMenuItemMongo) {
        return this.operations.save(scoreMenuItemMongo);
    }

    private Mono<ScoreCategoryMongo> saveCategoryItem(ScoreCategoryMongo scoreCategoryMongo) {
        return this.operations.save(scoreCategoryMongo);
    }

    private ScoreMenuItem mapperMenuItem(ScoreMenuItemMongo scoreMenuItemMongo) {
        return new ScoreMenuItem(scoreMenuItemMongo.getMenuUuid(), BigDecimal.valueOf(scoreMenuItemMongo.getScore()).setScale(9, RoundingMode.HALF_UP));
    }

    private ScoreMenuItemMongo mapperMenuItem(ScoreMenuItem scoreMenuItem) {
        return new ScoreMenuItemMongo(scoreMenuItem.getMenuUuid(), scoreMenuItem.getScore().doubleValue());
    }

    private ScoreCategory mapperCategory(ScoreCategoryMongo scoreCategoryMongo) {
        return new ScoreCategory(scoreCategoryMongo.getCategory(), BigDecimal.valueOf(scoreCategoryMongo.getScore()).setScale(9, RoundingMode.HALF_UP));
    }

    private ScoreCategoryMongo mapperCategory(ScoreCategory scoreCategory) {
        return new ScoreCategoryMongo(scoreCategory.getCategory(), scoreCategory.getScore().doubleValue());
    }
}
