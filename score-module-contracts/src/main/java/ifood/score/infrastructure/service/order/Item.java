package ifood.score.infrastructure.service.order;

import ifood.score.menu.Category;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of={"quantity", "menuUuid"})
public class Item implements Serializable {
	
	private static final long serialVersionUID = 4164408761280047513L;
	
	private Integer quantity;
	private UUID menuUuid;
	private BigDecimal menuUnitPrice;
	private Category menuCategory;
	
}
