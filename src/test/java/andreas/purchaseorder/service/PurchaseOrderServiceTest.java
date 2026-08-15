package andreas.purchaseorder.service;

import andreas.purchaseorder.dto.request.purchase.PurchaseOrderDetailRequest;
import andreas.purchaseorder.dto.request.purchase.PurchaseOrderRequest;
import andreas.purchaseorder.dto.response.PODetailWithHeaderResponse;
import andreas.purchaseorder.entity.Item;
import andreas.purchaseorder.entity.PurchaseOrderDetail;
import andreas.purchaseorder.entity.PurchaseOrderHeader;
import andreas.purchaseorder.exception.customException.ResourceNotFoundException;
import andreas.purchaseorder.repository.ItemRepository;
import andreas.purchaseorder.repository.PurchaseOrderHeaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderHeaderRepository poHeaderRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private PurchaseOrderService poService;

    private Item item;
    private PurchaseOrderHeader header;

    @BeforeEach
    void setUp() {
        item = Item.builder().id(1).name("Mouse").price(100000).cost(80000).build();

        header = PurchaseOrderHeader.builder()
                .id(10)
                .description("Restock")
                .details(new ArrayList<>())
                .build();
    }

    @Test
    void create_WhenValidRequest_ShouldCalculateTotalAndSave() {

        PurchaseOrderDetailRequest detailRequest = new PurchaseOrderDetailRequest(1, 5);
        PurchaseOrderRequest request = new PurchaseOrderRequest("New Order", List.of(detailRequest));

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(poHeaderRepository.save(any(PurchaseOrderHeader.class))).thenAnswer(i -> i.getArgument(0));

        PODetailWithHeaderResponse response = poService.create(request, "admin");

        assertThat(response.header().totalPrice()).isEqualTo(500000);
        assertThat(response.header().totalCost()).isEqualTo(400000);
        assertThat(response.details()).hasSize(1);

    }

    @Test
    void findById_WhenPOExists_ShouldReturnPODetails() {

        when(poHeaderRepository.findByIdWithDetails(10)).thenReturn(Optional.of(header));

        PODetailWithHeaderResponse response = poService.findById(10);

        assertThat(response).isNotNull();
        assertThat(response.header().id()).isEqualTo(10);
        assertThat(response.header().description()).isEqualTo("Restock");
        verify(poHeaderRepository, times(1)).findByIdWithDetails(10);

    }

    @Test
    void update_WhenValidRequest_ShouldClearOldDetailsAndCalculateNew() {

        header.addDetail(new PurchaseOrderDetail());

        PurchaseOrderDetailRequest newDetailRequest = new PurchaseOrderDetailRequest(1, 10);
        PurchaseOrderRequest request = new PurchaseOrderRequest("Updated Order", List.of(newDetailRequest));

        when(poHeaderRepository.findByIdWithDetails(10)).thenReturn(Optional.of(header));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(poHeaderRepository.save(any(PurchaseOrderHeader.class))).thenReturn(header);

        PODetailWithHeaderResponse response = poService.update(10, request, "admin");

        assertThat(response.header().description()).isEqualTo("Updated Order");
        assertThat(response.header().totalPrice()).isEqualTo(1000000);
        assertThat(response.details()).hasSize(1);

    }

    @Test
    void findById_WhenExists_ShouldReturnDetails() {

        when(poHeaderRepository.findByIdWithDetails(10)).thenReturn(Optional.of(header));
        PODetailWithHeaderResponse response = poService.findById(10);
        assertThat(response.header().id()).isEqualTo(10);

    }

    @Test
    void delete_WhenExists_ShouldDeletePO() {

        when(poHeaderRepository.findById(10)).thenReturn(Optional.of(header));
        poService.delete(10);
        verify(poHeaderRepository).delete(header);

    }

    @Test
    void findById_WhenPONotFound_ShouldThrowResourceNotFoundException() {

        when(poHeaderRepository.findByIdWithDetails(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> poService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("could not be found");

        verify(poHeaderRepository, times(1)).findByIdWithDetails(99);
    }

    @Test
    void create_WhenItemNotFound_ShouldThrowException() {

        PurchaseOrderDetailRequest detailRequest = new PurchaseOrderDetailRequest(99, 5);
        PurchaseOrderRequest request = new PurchaseOrderRequest("New Order", List.of(detailRequest));

        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> poService.create(request, "admin"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("could not be found");

        verify(poHeaderRepository, never()).save(any());

    }

    @Test
    void update_WhenPONotFound_ShouldThrowException() {

        PurchaseOrderRequest request = new PurchaseOrderRequest("Draft", List.of());
        when(poHeaderRepository.findByIdWithDetails(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> poService.update(99, request, "admin"))
                .isInstanceOf(ResourceNotFoundException.class);

    }
}
