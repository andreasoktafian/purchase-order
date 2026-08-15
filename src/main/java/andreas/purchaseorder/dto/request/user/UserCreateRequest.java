package andreas.purchaseorder.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserCreateRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        @Email(message = "Invalid email format")
        String email,

        String phone
) {
}
