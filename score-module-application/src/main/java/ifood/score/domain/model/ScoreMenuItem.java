package ifood.score.domain.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ScoreMenuItem implements Score, Serializable {

    private static final long serialVersionUID = -3955505984070092761L;

    @NonNull
    private UUID menuUuid;

    @NonNull
    private BigDecimal score;

}
