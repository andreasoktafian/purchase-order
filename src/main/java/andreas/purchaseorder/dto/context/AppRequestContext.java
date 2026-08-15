package andreas.purchaseorder.dto.context;

public record AppRequestContext(

        String actionBy,
        String correlationId

) {
}
