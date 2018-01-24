package ifood.score.domain.repository.entity;

import ifood.score.menu.Category;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RelevanceCategoryMongo {

    private Category category;
    private Double relevance;

}
