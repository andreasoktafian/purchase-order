package andreas.purchaseorder.dto.response;

import andreas.purchaseorder.entity.PurchaseOrderHeader;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record POHeaderResponse(

        Integer id,
        LocalDateTime datetime,
        String description,
        Integer totalPrice,
        Integer totalCost,
        String createdBy,
        String updatedBy,
        LocalDateTime createdDatetime,
        LocalDateTime updatedDatetime

) {

    public static POHeaderResponse fromEntity(PurchaseOrderHeader entity) {
        return new POHeaderResponse(
                entity.getId(),
                entity.getDatetime(),
                entity.getDescription(),
                entity.getTotalPrice(),
                entity.getTotalCost(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedDatetime(),
                entity.getUpdatedDatetime()
        );
    }

}
