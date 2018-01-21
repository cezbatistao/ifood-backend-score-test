package ifood.score.service;

import ifood.score.domain.model.Score;
import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
import ifood.score.menu.Category;

import java.math.BigDecimal;
import java.util.UUID;

class ScoreFactory {
    public static Score getScore(Object key, BigDecimal score){
        if(key instanceof Category) {
            return new ScoreCategory((Category) key, score);
        } else if(key instanceof UUID) {
            return new ScoreMenuItem((UUID) key, score);
        }

        throw new IllegalArgumentException(String.format("Score can't created with key [%s] of type [%s]", key, key.getClass().getName()));
    }
}
