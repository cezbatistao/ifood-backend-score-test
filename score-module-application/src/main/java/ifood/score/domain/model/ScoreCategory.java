package ifood.score.domain.model;

import ifood.score.menu.Category;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ScoreCategory implements Score, Serializable {

    private static final long serialVersionUID = -1642544040261608617L;

    @NonNull
    private Category category;

    @NonNull
    private BigDecimal score;

}
