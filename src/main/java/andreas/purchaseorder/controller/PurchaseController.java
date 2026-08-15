package andreas.purchaseorder.controller;

import andreas.purchaseorder.dto.context.AppRequestContext;
import andreas.purchaseorder.dto.request.purchase.PurchaseOrderRequest;
import andreas.purchaseorder.dto.response.BaseResponse;
import andreas.purchaseorder.dto.response.PODetailWithHeaderResponse;
import andreas.purchaseorder.dto.response.POHeaderResponse;
import andreas.purchaseorder.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<POHeaderResponse>>> getItems(
            AppRequestContext context,
            Pageable pageable) {

        Page<POHeaderResponse> purchaseOrders = purchaseOrderService.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.success(purchaseOrders, "All purchase orders retrieved successfully"));

    }

    @GetMapping(params = "id")
    public ResponseEntity<BaseResponse<PODetailWithHeaderResponse>> getPurchaseOrderByIdParam(
            AppRequestContext context,
            @RequestParam Integer id) {

        PODetailWithHeaderResponse response = purchaseOrderService.findById(id);
        return ResponseEntity.ok(BaseResponse.success(response, "Purchase order detail retrieved successfully"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PODetailWithHeaderResponse>> getPurchaseOrderByIdPath(
            AppRequestContext context,
            @PathVariable Integer id) {

        PODetailWithHeaderResponse response = purchaseOrderService.findById(id);
        return ResponseEntity.ok(BaseResponse.success(response, "Purchase order detail retrieved successfully"));

    }

    @PostMapping
    public ResponseEntity<BaseResponse<PODetailWithHeaderResponse>> create(
            AppRequestContext context,
            @Valid @RequestBody PurchaseOrderRequest request) {

        PODetailWithHeaderResponse response = purchaseOrderService.create(request, context.actionBy());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.header().id())
                .toUri();

        return ResponseEntity.created(location).body(BaseResponse.success(response, "Purchase order created successfully"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PODetailWithHeaderResponse>> update(
            AppRequestContext context,
            @PathVariable Integer id,
            @Valid @RequestBody PurchaseOrderRequest request) {

        PODetailWithHeaderResponse response = purchaseOrderService.update(id, request, context.actionBy());
        return ResponseEntity.ok(BaseResponse.success(response, "Purchase Order updated successfully"));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            AppRequestContext context,
            @PathVariable Integer id) {

        purchaseOrderService.delete(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Purchase Order deleted successfully"));

    }

}
