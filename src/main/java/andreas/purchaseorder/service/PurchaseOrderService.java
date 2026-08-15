package andreas.purchaseorder.service;

import andreas.purchaseorder.annotation.LogBusinessEvent;
import andreas.purchaseorder.dto.request.purchase.PurchaseOrderDetailRequest;
import andreas.purchaseorder.dto.request.purchase.PurchaseOrderRequest;
import andreas.purchaseorder.dto.response.PODetailWithHeaderResponse;
import andreas.purchaseorder.dto.response.POHeaderResponse;
import andreas.purchaseorder.entity.Item;
import andreas.purchaseorder.entity.PurchaseOrderDetail;
import andreas.purchaseorder.entity.PurchaseOrderHeader;
import andreas.purchaseorder.exception.customException.ResourceNotFoundException;
import andreas.purchaseorder.repository.ItemRepository;
import andreas.purchaseorder.repository.PurchaseOrderHeaderRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderHeaderRepository poHeaderRepository;
    private final ItemRepository itemRepository;

    @LogBusinessEvent("FIND_ALL_PURCHASE_ORDERS")
    public Page<POHeaderResponse> findAll(Pageable pageable) {
        return poHeaderRepository.findAll(pageable).map(POHeaderResponse::fromEntity);
    }

    @LogBusinessEvent("FIND_PURCHASE_ORDER_BY_ID")
    public PODetailWithHeaderResponse findById(Integer id) {

        PurchaseOrderHeader header = poHeaderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order with ID " + id + " could not be found"));

        return PODetailWithHeaderResponse.fromEntity(header);

    }

    @Transactional
    @LogBusinessEvent("CREATE_PURCHASE_ORDER")
    public PODetailWithHeaderResponse create(@NonNull PurchaseOrderRequest request, String actionBy) {

        PurchaseOrderHeader header = PurchaseOrderHeader.builder()
                .datetime(LocalDateTime.now())
                .description(request.description())
                .createdBy(actionBy)
                .build();

        processOrderDetails(header, request.details());

        PurchaseOrderHeader savedHeader = poHeaderRepository.save(header);

        return PODetailWithHeaderResponse.fromEntity(savedHeader);

    }

    @Transactional
    @LogBusinessEvent("UPDATE_PURCHASE_ORDER")
    public PODetailWithHeaderResponse update(Integer id, @NonNull PurchaseOrderRequest request, String actionBy) {

        PurchaseOrderHeader header = poHeaderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order with ID " + id + " could not be found"));


        header.setDescription(request.description());
        header.setUpdatedBy(actionBy);
        header.getDetails().clear();

        processOrderDetails(header, request.details());

        PurchaseOrderHeader updatedHeader = poHeaderRepository.save(header);

        return PODetailWithHeaderResponse.fromEntity(updatedHeader);

    }

    @Transactional
    @LogBusinessEvent("DELETE_PURCHASE_ORDER")
    public void delete(Integer id) {

        PurchaseOrderHeader header = poHeaderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order with ID " + id + " could not be found"));

        poHeaderRepository.delete(header);

    }

    private void processOrderDetails(PurchaseOrderHeader header, List<PurchaseOrderDetailRequest> detailRequests) {

        int totalCost = 0;
        int totalPrice = 0;

        for (PurchaseOrderDetailRequest detailRequest : detailRequests) {
            Item item = itemRepository.findById(detailRequest.itemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Item with ID " + detailRequest.itemId() + " could not be found"));

            int qty = detailRequest.itemQty();

            totalCost += (item.getCost() * qty);
            totalPrice += (item.getPrice() * qty);

            PurchaseOrderDetail detail = PurchaseOrderDetail.builder()
                    .item(item)
                    .itemQty(qty)
                    .itemCost(item.getCost())
                    .itemPrice(item.getPrice())
                    .build();

            header.addDetail(detail);
        }

        header.setTotalCost(totalCost);
        header.setTotalPrice(totalPrice);

    }

}