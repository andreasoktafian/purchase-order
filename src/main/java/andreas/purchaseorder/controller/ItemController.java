package andreas.purchaseorder.controller;

import andreas.purchaseorder.dto.context.AppRequestContext;
import andreas.purchaseorder.dto.request.item.ItemCreateRequest;
import andreas.purchaseorder.dto.request.item.ItemUpdateRequest;
import andreas.purchaseorder.dto.response.BaseResponse;
import andreas.purchaseorder.dto.response.ItemResponse;
import andreas.purchaseorder.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ItemResponse>>> getItems(
            AppRequestContext context,
            Pageable pageable) {

        var items = itemService.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.success(items, "All items retrieved successfully"));

    }

    @GetMapping(params = "id")
    public ResponseEntity<BaseResponse<ItemResponse>> getItemByIdParam(
            AppRequestContext context,
            @RequestParam Integer id) {

        var item = itemService.findById(id);
        return ResponseEntity.ok(BaseResponse.success(item, "Item retrieved successfully"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ItemResponse>> getItemByIdPath(
            AppRequestContext context,
            @PathVariable Integer id) {

        var item = itemService.findById(id);
        return ResponseEntity.ok(BaseResponse.success(item, "Item retrieved successfully"));

    }

    @PostMapping
    public ResponseEntity<BaseResponse<ItemResponse>> create(
            AppRequestContext context,
            @Valid @RequestBody ItemCreateRequest request) {

        var item = itemService.create(request, context.actionBy());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .queryParam("id", item.id())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(BaseResponse.success(item, "Item created successfully"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ItemResponse>> update(
            AppRequestContext context,
            @PathVariable Integer id,
            @Valid @RequestBody ItemUpdateRequest request) {

        var user = itemService.update(id, request, context.actionBy());
        return ResponseEntity.ok(BaseResponse.success(user, "Item updated successfully"));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            AppRequestContext context,
            @PathVariable Integer id) {

        itemService.delete(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Item deleted successfully"));

    }

}
