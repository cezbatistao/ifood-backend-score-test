package ifood.score.domain.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Document(collection = "orderRelevance")
public class OrderRelevanceMongo {

    @Id
    private UUID orderUuid;
    private List<RelevanceMenuItemMongo> relevancesMenuItem;
    private List<RelevanceCategoryMongo> relevancesCategory;

}
