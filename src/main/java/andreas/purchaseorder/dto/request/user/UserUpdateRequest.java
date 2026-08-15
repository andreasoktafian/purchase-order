package andreas.purchaseorder.dto.request.user;

import jakarta.validation.constraints.Email;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserUpdateRequest(

        String firstName,
        String lastName,

        @Email(message = "Invalid email format")
        String email,

        String phone
) {
}
