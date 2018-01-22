package ifood.score.domain.repository.entity;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Document(collection = "order")
public class OrderMongo implements Serializable {

    private static final long serialVersionUID = 8746743142400129467L;

    @Id @NonNull
    private UUID uuid;

    @NonNull
    private UUID customerUuid;

    @NonNull
    private UUID restaurantUuid;

    @NonNull
    private UUID addressUuid;

    @NonNull
    private List<ItemMongo> items;

    private Date confirmedAt;
    private StatusOrder status = StatusOrder.ACTIVE;

    public OrderMongo() {
    }
}
