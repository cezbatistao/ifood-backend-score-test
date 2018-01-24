package ifood.score.domain.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Document(collection = "scoreMenuItem")
public class ScoreMenuItemMongo implements Serializable {

    private static final long serialVersionUID = -839224893062958534L;

    @Id @NonNull
    private UUID menuUuid;

    @NonNull
    private BigDecimal score;
}
