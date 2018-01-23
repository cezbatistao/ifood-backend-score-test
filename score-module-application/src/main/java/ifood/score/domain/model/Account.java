package ifood.score.domain.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class Account implements Serializable {

    private static final long serialVersionUID = -304445207927210885L;

    @NonNull
    private List<ScoreMenuItem> scoreMenuItems;

    @NonNull
    private List<ScoreCategory> scoreCategories;

}
