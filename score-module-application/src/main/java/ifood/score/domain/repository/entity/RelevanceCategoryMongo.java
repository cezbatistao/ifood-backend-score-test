package ifood.score.domain.repository.entity;

import ifood.score.menu.Category;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RelevanceCategoryMongo {

    private Category category;
    private BigDecimal relevance;

}
