package andreas.purchaseorder.service;

import andreas.purchaseorder.annotation.LogBusinessEvent;
import andreas.purchaseorder.dto.request.item.ItemCreateRequest;
import andreas.purchaseorder.dto.request.item.ItemUpdateRequest;
import andreas.purchaseorder.dto.response.ItemResponse;
import andreas.purchaseorder.entity.Item;
import andreas.purchaseorder.exception.customException.BusinessException;
import andreas.purchaseorder.exception.customException.ConflictException;
import andreas.purchaseorder.exception.customException.ResourceNotFoundException;
import andreas.purchaseorder.repository.ItemRepository;
import andreas.purchaseorder.repository.PurchaseOrderDetailRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final PurchaseOrderDetailRepository poDetailRepository;

    @LogBusinessEvent("FIND_ALL_ITEMS")
    public Page<ItemResponse> findAll(Pageable pageable) {

        return itemRepository.findAll(pageable).map(ItemResponse::fromEntity);

    }

    @LogBusinessEvent("FIND_ITEM_BY_ID")
    public ItemResponse findById(Integer id) {

        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + id + " could not be found"));

        return ItemResponse.fromEntity(item);

    }

    @LogBusinessEvent("CREATE_ITEM")
    public ItemResponse create(@NonNull ItemCreateRequest request, String actionBy) {

        validateNewitem(request);

        var item = Item.builder()
                .id(generateUniqueRandomId())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .cost(request.cost())
                .createdBy(actionBy)
                .build();

        return ItemResponse.fromEntity(itemRepository.save(item));

    }

    @LogBusinessEvent("UPDATE_ITEM")
    public ItemResponse update(Integer id, @NonNull ItemUpdateRequest request, String actionBy) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + id + " could not be found"));

        validateExistingItems(request, item);
        applyItemUpdates(request, item,actionBy);

        return ItemResponse.fromEntity(itemRepository.save(item));

    }

    @LogBusinessEvent("DELETE_ITEM")
    public void delete(Integer id) {

        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + id + " could not be found"));

        if (poDetailRepository.existsByItemId(id)) {
            throw new ConflictException("Cannot delete Item with ID " + id + " because it is already used in existing Purchase Orders");
        }

        itemRepository.delete(item);

    }

    private Integer generateUniqueRandomId() {
        Integer randomId;
        boolean isDuplicate;

        do {
            randomId = ThreadLocalRandom.current().nextInt(1000, 1_999_999_999);

            isDuplicate = itemRepository.existsById(randomId);

        } while (isDuplicate);

        return randomId;
    }

    private void validateNewitem(@NonNull ItemCreateRequest request) {

        if (StringUtils.hasText(request.name()) && itemRepository.existsByName(request.name())) {
            throw new ConflictException("Name " + request.name() + " is already in use");
        }

    }

    private void validateExistingItems(@NonNull ItemUpdateRequest request, Item item) {

        if (StringUtils.hasText(request.name())
                && !request.name().equals(item.getName())
                && itemRepository.existsByNameAndIdNot(request.name(), item.getId())) {
            throw new ConflictException("Name " + request.name() + " is already in use");
        }

        int effectivePrice = request.price() != null ? request.price() : item.getPrice();
        int effectiveCost = request.cost() != null ? request.cost() : item.getCost();

        if (effectivePrice < effectiveCost) {
            throw new BusinessException(
                    "Invalid update: The resulting price (" + effectivePrice +
                            ") cannot be less than the resulting cost (" + effectiveCost + ")"
            );
        }

        if (effectivePrice < 0 || effectiveCost < 0) {
            throw new BusinessException("Price and cost cannot be negative values");
        }

    }

    private void applyItemUpdates(@NonNull ItemUpdateRequest request, Item item, String actionBy) {

        boolean isModified = false;

        if (StringUtils.hasText(request.name()) && !request.name().equals(item.getName())) {
            item.setName(request.name());
            isModified = true;
        }

        if (request.description() != null && !request.description().equals(item.getDescription())) {
            item.setDescription(request.description());
            isModified = true;
        }

        if (request.price() != null && !request.price().equals(item.getPrice())) {
            item.setPrice(request.price());
            isModified = true;
        }

        if (request.cost() != null && !request.cost().equals(item.getCost())) {
            item.setCost(request.cost());
            isModified = true;
        }

        if (isModified) {
            item.setUpdatedBy(actionBy);
        }

    }

}