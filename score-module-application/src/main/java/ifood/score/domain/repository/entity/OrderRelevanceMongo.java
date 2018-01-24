package ifood.score.domain.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "orderRelevance")
public class OrderRelevanceMongo {

    @Id
    private UUID orderUuid;
    private List<RelevanceMenuItemMongo> relevancesMenuItem;
    private List<RelevanceCategoryMongo> relevancesCategory;
    private StatusOrder status = StatusOrder.ACTIVE;

    public OrderRelevanceMongo(UUID orderUuid, List<RelevanceMenuItemMongo> relevancesMenuItem, List<RelevanceCategoryMongo> relevancesCategory) {
        this.orderUuid = orderUuid;
        this.relevancesMenuItem = relevancesMenuItem;
        this.relevancesCategory = relevancesCategory;
    }
}
