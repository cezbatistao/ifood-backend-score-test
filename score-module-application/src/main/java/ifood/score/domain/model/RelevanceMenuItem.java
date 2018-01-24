package ifood.score.domain.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
@EqualsAndHashCode
public class RelevanceMenuItem implements Relevance<UUID>, Serializable {

    private static final long serialVersionUID = 270175622950663989L;

    @NonNull
    private UUID menuUuid;

    @NonNull
    private BigDecimal relevance;

    @Override
    public UUID getKey() {
        return menuUuid;
    }
}
