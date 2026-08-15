package andreas.purchaseorder.controller;

import andreas.purchaseorder.dto.context.AppRequestContext;
import andreas.purchaseorder.dto.request.item.ItemCreateRequest;
import andreas.purchaseorder.dto.request.item.ItemUpdateRequest;
import andreas.purchaseorder.dto.response.BaseResponse;
import andreas.purchaseorder.dto.response.ItemResponse;
import andreas.purchaseorder.exception.customException.BusinessException;
import andreas.purchaseorder.exception.customException.ConflictException;
import andreas.purchaseorder.service.ItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private ItemResponse itemResponse;
    private AppRequestContext requestContext;

    @BeforeEach
    void setUp() {
        requestContext = new AppRequestContext("admin", "test-corr-id");
        itemResponse = new ItemResponse(500, "Keyboard", "RGB", 150000, 100000, "admin", null, null, null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getItems_ShouldReturn200() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemResponse> page = new PageImpl<>(List.of(itemResponse));
        when(itemService.findAll(pageable)).thenReturn(page);

        ResponseEntity<BaseResponse<Page<ItemResponse>>> response = itemController.getItems(requestContext, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void getItemByIdParam_ShouldReturn200() {

        when(itemService.findById(500)).thenReturn(itemResponse);

        ResponseEntity<BaseResponse<ItemResponse>> response = itemController.getItemByIdParam(requestContext, 500);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void getItemByIdPath_ShouldReturn200() {

        when(itemService.findById(500)).thenReturn(itemResponse);

        ResponseEntity<BaseResponse<ItemResponse>> response = itemController.getItemByIdPath(requestContext, 500);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void create_ShouldReturn201() {

        ItemCreateRequest request = new ItemCreateRequest("Keyboard", "RGB", 150000, 100000);
        when(itemService.create(request, "admin")).thenReturn(itemResponse);

        ResponseEntity<BaseResponse<ItemResponse>> response = itemController.create(requestContext, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

    }

    @Test
    void update_ShouldReturn200() {

        ItemUpdateRequest request = new ItemUpdateRequest("Keyboard Pro", null, 200000, 150000);
        when(itemService.update(500, request, "admin")).thenReturn(itemResponse);

        ResponseEntity<BaseResponse<ItemResponse>> response = itemController.update(requestContext, 500, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void delete_ShouldReturn200() {

        doNothing().when(itemService).delete(500);

        ResponseEntity<BaseResponse<Void>> response = itemController.delete(requestContext, 500);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void create_WhenNameConflict_ShouldThrowException() {

        ItemCreateRequest request = new ItemCreateRequest("Keyboard", "RGB", 150000, 100000);
        when(itemService.create(request, "admin")).thenThrow(new ConflictException("Item name already exists"));

        assertThatThrownBy(() -> itemController.create(requestContext, request))
                .isInstanceOf(ConflictException.class);

    }

    @Test
    void update_WhenPriceLowerThanCost_ShouldThrowBusinessException() {

        ItemUpdateRequest request = new ItemUpdateRequest(null, null, 50000, 100000);
        when(itemService.update(500, request, "admin")).thenThrow(new BusinessException("Price cannot be less than cost"));

        assertThatThrownBy(() -> itemController.update(requestContext, 500, request))
                .isInstanceOf(BusinessException.class);

    }

    @Test
    void delete_WhenItemUsedInPO_ShouldThrowConflictException() {

        doThrow(new ConflictException("Item used in PO")).when(itemService).delete(500);

        assertThatThrownBy(() -> itemController.delete(requestContext, 500))
                .isInstanceOf(ConflictException.class);

    }
}