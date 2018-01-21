package ifood.score.domain.model;

import java.math.BigDecimal;

public interface Relevance<T> {

    T getKey();
    BigDecimal getRelevance();

}
