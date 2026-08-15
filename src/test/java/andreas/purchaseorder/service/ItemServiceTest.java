package andreas.purchaseorder.service;

import andreas.purchaseorder.dto.request.item.ItemCreateRequest;
import andreas.purchaseorder.dto.request.item.ItemUpdateRequest;
import andreas.purchaseorder.dto.response.ItemResponse;
import andreas.purchaseorder.entity.Item;
import andreas.purchaseorder.exception.customException.BusinessException;
import andreas.purchaseorder.exception.customException.ConflictException;
import andreas.purchaseorder.exception.customException.ResourceNotFoundException;
import andreas.purchaseorder.repository.ItemRepository;
import andreas.purchaseorder.repository.PurchaseOrderDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseOrderDetailRepository poDetailRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(500)
                .name("Keyboard")
                .price(150000)
                .cost(100000)
                .build();
    }

    @Test
    void findAll_ShouldReturnPagedItems() {

        when(itemRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(item)));
        Page<ItemResponse> result = itemService.findAll(PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);

    }

    @Test
    void findById_WhenItemExists_ShouldReturnItem() {

        when(itemRepository.findById(500)).thenReturn(Optional.of(item));

        ItemResponse result = itemService.findById(500);

        assertThat(result.id()).isEqualTo(500);
        assertThat(result.name()).isEqualTo("Keyboard");
        assertThat(result.price()).isEqualTo(150000);
        assertThat(result.cost()).isEqualTo(100000);

    }

    @Test
    void create_WhenValidRequest_ShouldSaveItem() {

        ItemCreateRequest request = new ItemCreateRequest("Mouse", "Wireless", 50000, 30000);

        when(itemRepository.existsByName("Mouse")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemResponse result = itemService.create(request, "admin");
        assertThat(result.name()).isEqualTo("Mouse");

    }

    @Test
    void update_WhenValidRequest_ShouldUpdateItem() {

        ItemUpdateRequest request = new ItemUpdateRequest("Keyboard RGB", null, 200000, 150000);

        when(itemRepository.findById(500)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponse result = itemService.update(500, request, "admin");
        assertThat(result.name()).isEqualTo("Keyboard RGB");
        assertThat(result.price()).isEqualTo(200000);

    }

    @Test
    void delete_WhenNotUsedInPO_ShouldDeleteItem() {

        when(itemRepository.findById(500)).thenReturn(Optional.of(item));
        when(poDetailRepository.existsByItemId(500)).thenReturn(false);

        itemService.delete(500);
        verify(itemRepository).delete(item);

    }

    @Test
    void findById_WhenItemNotFound_ShouldThrowResourceNotFoundException() {

        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("could not be found");

        verify(itemRepository, times(1)).findById(99);
    }

    @Test
    void create_WhenNameAlreadyExists_ShouldThrowConflictException() {

        ItemCreateRequest request = new ItemCreateRequest("Keyboard", null, 50000, 30000);
        when(itemRepository.existsByName("Keyboard")).thenReturn(true);

        assertThatThrownBy(() -> itemService.create(request, "admin"))
                .isInstanceOf(ConflictException.class);

    }

    @Test
    void update_WhenPriceIsLowerThanCost_ShouldThrowBusinessException() {

        ItemUpdateRequest request = new ItemUpdateRequest(null, null, 50000, null);
        when(itemRepository.findById(500)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(500, request, "admin"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be less than the resulting cost");

    }

    @Test
    void update_WhenNegativeValue_ShouldThrowBusinessException() {

        ItemUpdateRequest request = new ItemUpdateRequest(null, null, null, -100);

        when(itemRepository.findById(500)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(500, request, "admin"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Price and cost cannot be negative");

    }

    @Test
    void delete_WhenItemUsedInPO_ShouldThrowConflictException() {

        when(itemRepository.findById(500)).thenReturn(Optional.of(item));
        when(poDetailRepository.existsByItemId(500)).thenReturn(true);

        assertThatThrownBy(() -> itemService.delete(500))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already used in existing Purchase Orders");

    }

}
