package andreas.purchaseorder.dto.response;

import andreas.purchaseorder.entity.PurchaseOrderDetail;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PODetailResponse(

        Integer id,
        Integer itemId,
        Integer itemQty,
        Integer itemPrice,
        Integer itemCost

) {

    public static PODetailResponse fromEntity(PurchaseOrderDetail entity) {
        return new PODetailResponse(
                entity.getId(),
                entity.getItem() != null ? entity.getItem().getId() : null,
                entity.getItemQty(),
                entity.getItemPrice(),
                entity.getItemCost()
        );
    }

}
