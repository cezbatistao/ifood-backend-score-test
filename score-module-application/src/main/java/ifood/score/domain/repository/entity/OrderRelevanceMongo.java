package ifood.score.domain.repository.entity;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Document(collection = "orderRelevance")
public class OrderRelevanceMongo {

    @Id
    @NonNull
    private UUID orderUuid;

    @NonNull
    private List<RelevanceMenuItemMongo> relevancesMenuItem;

    @NonNull
    private List<RelevanceCategoryMongo> relevancesCategory;

    private StatusOrder status = StatusOrder.ACTIVE;

}
