package andreas.purchaseorder.dto.response;

import andreas.purchaseorder.entity.User;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserResponse (
        Integer id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String createdBy,
        String updatedBy,
        LocalDateTime createdDatetime,
        LocalDateTime updatedDatetime
) {

    public static UserResponse fromEntity(User entity) {
        return new UserResponse(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedDatetime(),
                entity.getUpdatedDatetime()
        );
    }

}
