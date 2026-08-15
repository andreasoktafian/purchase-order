package andreas.purchaseorder.dto.response;

import andreas.purchaseorder.entity.Item;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ItemResponse(

        Integer id,
        String name,
        String description,
        Integer price,
        Integer cost,
        String createdBy,
        String updatedBy,
        LocalDateTime createdDatetime,
        LocalDateTime updatedDatetime

) {

    public static ItemResponse fromEntity(Item entity) {
        return new ItemResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCost(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedDatetime(),
                entity.getUpdatedDatetime()
        );
    }

}
