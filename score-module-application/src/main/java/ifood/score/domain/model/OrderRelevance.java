package ifood.score.domain.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(of = "orderUuid")
public class OrderRelevance implements Serializable {

    private static final long serialVersionUID = -645768404027179788L;

    @NonNull
    private UUID orderUuid;

    @NonNull
    private List<RelevanceMenuItem> relevancesMenuItem;

    @NonNull
    private List<RelevanceCategory> relevancesCategory;

}
