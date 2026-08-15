package andreas.purchaseorder.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse<E>(
        String type,
        E details
) {
}
