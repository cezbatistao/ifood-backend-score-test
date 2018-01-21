package ifood.score.domain.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Document(collection = "order")
public class OrderMongo implements Serializable {

    private static final long serialVersionUID = 8746743142400129467L;

    @Id
    private UUID uuid;
    private UUID customerUuid;
    private UUID restaurantUuid;
    private UUID addressUuid;
    private Date confirmedAt;
    private List<ItemMongo> items;

    public OrderMongo() {
    }
}
