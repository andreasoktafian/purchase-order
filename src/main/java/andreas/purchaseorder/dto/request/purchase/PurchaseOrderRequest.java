package andreas.purchaseorder.dto.request.purchase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PurchaseOrderRequest(

        String description,

        @NotEmpty(message = "Purchase order must have at least one detail")
        @Valid
        List<PurchaseOrderDetailRequest> details
) {
}
