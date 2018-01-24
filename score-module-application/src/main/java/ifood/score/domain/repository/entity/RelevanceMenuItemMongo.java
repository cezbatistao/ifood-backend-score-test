package ifood.score.domain.repository.entity;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RelevanceMenuItemMongo {

    private UUID menuUuid;
    private Double relevance;

}
