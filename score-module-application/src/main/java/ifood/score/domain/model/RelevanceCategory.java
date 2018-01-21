package ifood.score.domain.model;

import ifood.score.menu.Category;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class RelevanceCategory implements Relevance<Category>, Serializable {

    private static final long serialVersionUID = -2349722705772529444L;

    @NonNull
    private Category category;

    @NonNull
    private BigDecimal relevance;

    @Override
    public Category getKey() {
        return category;
    }
}
