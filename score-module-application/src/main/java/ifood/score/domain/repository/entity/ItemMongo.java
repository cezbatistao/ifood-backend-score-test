package ifood.score.domain.repository.entity;

import ifood.score.menu.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ItemMongo implements Serializable {

    private static final long serialVersionUID = -6039463850419238137L;

    private Integer quantity;
    private UUID menuUuid;
    private BigDecimal menuUnitPrice;
    private Category menuCategory;

    public ItemMongo() {
    }
}