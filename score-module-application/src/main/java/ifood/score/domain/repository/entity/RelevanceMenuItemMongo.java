package ifood.score.domain.repository.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RelevanceMenuItemMongo {

    private UUID menuUuid;
    private BigDecimal relevance;

}
