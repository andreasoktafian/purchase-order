package andreas.purchaseorder.dto.response;

import andreas.purchaseorder.entity.PurchaseOrderHeader;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PODetailWithHeaderResponse(

        POHeaderResponse header,
        List<PODetailResponse> details

) {

    public static PODetailWithHeaderResponse fromEntity(PurchaseOrderHeader entity) {
        List<PODetailResponse> detailResponses = entity.getDetails()
                .stream()
                .map(PODetailResponse::fromEntity)
                .toList();

        return new PODetailWithHeaderResponse(
                POHeaderResponse.fromEntity(entity),
                detailResponses
        );
    }

}
