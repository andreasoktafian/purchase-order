package andreas.purchaseorder.dto.request.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ItemUpdateRequest(
        String name,
        String description,

        @Min(value = 0, message = "Price cannot be negative")
        Integer price,

        @Min(value = 0, message = "Cost cannot be negative")
        Integer cost
) {

        @JsonIgnore
        @AssertTrue(message = "Price cannot be lower than cost")
        public boolean isPriceValid() {
                if (price == null || cost == null) {
                        return true;
                }

                return price >= cost;
        }

}
