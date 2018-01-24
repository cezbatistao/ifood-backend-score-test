package ifood.score.domain.repository;

import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
import ifood.score.domain.repository.entity.ScoreCategoryMongo;
import ifood.score.domain.repository.entity.ScoreMenuItemMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ScoreRepository {

    private ReactiveMongoOperations operations;

    @Autowired
    public ScoreRepository(ReactiveMongoOperations operations) {
        this.operations = operations;
    }

    public Flux<ScoreMenuItem> saveAllScoreMenuItem(List<ScoreMenuItem> scoreMenuItems) {
        return operations.insertAll(mapperMenuItemToMango(scoreMenuItems)).map(this::mapperMenuItem);
    }

    public Flux<ScoreCategory> saveAllCategory(List<ScoreCategory> scoreCategories) {
        return operations.insertAll(mapperCategoryToMango(scoreCategories)).map(this::mapperCategory);
    }

    public Flux<ScoreMenuItem> findAllScoreMenuItem() {
        return operations.findAll(ScoreMenuItemMongo.class).map(this::mapperMenuItem);
    }

    public Flux<ScoreCategory> findAllScoreCategory() {
        return operations.findAll(ScoreCategoryMongo.class).map(this::mapperCategory);
    }

    private List<ScoreMenuItemMongo> mapperMenuItemToMango(List<ScoreMenuItem> scoreMenuItems) {
        return scoreMenuItems
                .stream()
                .map(scoreMenuItem -> new ScoreMenuItemMongo(scoreMenuItem.getMenuUuid(), scoreMenuItem.getScore()))
                .collect(Collectors.toList());
    }

    private List<ScoreMenuItem> mapperMenuItem(List<ScoreMenuItemMongo> scoreMenuItensMongo) {
        return scoreMenuItensMongo
                .stream()
                .map(scoreMenuItemMongo -> new ScoreMenuItem(scoreMenuItemMongo.getMenuUuid(), scoreMenuItemMongo.getScore()))
                .collect(Collectors.toList());
    }

    private ScoreMenuItem mapperMenuItem(ScoreMenuItemMongo scoreMenuItemMongo) {
        return new ScoreMenuItem(scoreMenuItemMongo.getMenuUuid(), scoreMenuItemMongo.getScore());
    }

    private List<ScoreCategoryMongo> mapperCategoryToMango(List<ScoreCategory> scoreCategories) {
        return scoreCategories
                .stream()
                .map(scoreCategory -> new ScoreCategoryMongo(scoreCategory.getCategory(), scoreCategory.getScore()))
                .collect(Collectors.toList());
    }

    private List<ScoreCategory> mapperCategory(List<ScoreCategoryMongo> scoreCategoriesMongo) {
        return scoreCategoriesMongo
                .stream()
                .map(scoreCategoryMongo -> new ScoreCategory(scoreCategoryMongo.getCategory(), scoreCategoryMongo.getScore()))
                .collect(Collectors.toList());
    }

    private ScoreCategory mapperCategory(ScoreCategoryMongo scoreCategoryMongo) {
        return new ScoreCategory(scoreCategoryMongo.getCategory(), scoreCategoryMongo.getScore());
    }
}
