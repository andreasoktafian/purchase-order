package andreas.purchaseorder.dto.request.purchase;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PurchaseOrderDetailRequest(
        @NotNull(message = "Item ID is required")
        Integer itemId,

        @NotNull(message = "Item quantity is required")
        @Min(value = 1, message = "item_qty must be > 0")
        Integer itemQty
) {
}
