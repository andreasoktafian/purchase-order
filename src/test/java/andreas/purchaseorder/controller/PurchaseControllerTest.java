package andreas.purchaseorder.controller;

import andreas.purchaseorder.dto.context.AppRequestContext;
import andreas.purchaseorder.dto.request.purchase.PurchaseOrderDetailRequest;
import andreas.purchaseorder.dto.request.purchase.PurchaseOrderRequest;
import andreas.purchaseorder.dto.response.BaseResponse;
import andreas.purchaseorder.dto.response.PODetailWithHeaderResponse;
import andreas.purchaseorder.dto.response.POHeaderResponse;
import andreas.purchaseorder.exception.customException.ResourceNotFoundException;
import andreas.purchaseorder.service.PurchaseOrderService;
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
class PurchaseControllerTest {

    @Mock
    private PurchaseOrderService poService;

    @InjectMocks
    private PurchaseController purchaseController;

    private POHeaderResponse headerResponse;
    private PODetailWithHeaderResponse detailResponse;
    private AppRequestContext requestContext;

    @BeforeEach
    void setUp() {
        requestContext = new AppRequestContext("admin", "test-corr-id");
        headerResponse = new POHeaderResponse(10, null, "Restock", 500000, 400000, "admin", null, null, null);
        detailResponse = new PODetailWithHeaderResponse(headerResponse, List.of());

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getPurchaseOrders_ShouldReturn200() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<POHeaderResponse> page = new PageImpl<>(List.of(headerResponse));
        when(poService.findAll(pageable)).thenReturn(page);

        ResponseEntity<BaseResponse<Page<POHeaderResponse>>> response = purchaseController.getPurchaseOrders(requestContext, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void getPurchaseOrderByIdParam_ShouldReturn200() {

        when(poService.findById(10)).thenReturn(detailResponse);

        ResponseEntity<BaseResponse<PODetailWithHeaderResponse>> response = purchaseController.getPurchaseOrderByIdParam(requestContext, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void getPurchaseOrderByIdPath_ShouldReturn200() {

        when(poService.findById(10)).thenReturn(detailResponse);

        ResponseEntity<BaseResponse<PODetailWithHeaderResponse>> response = purchaseController.getPurchaseOrderByIdPath(requestContext, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void create_ShouldReturn201() {

        PurchaseOrderDetailRequest detail = new PurchaseOrderDetailRequest(1, 5);
        PurchaseOrderRequest request = new PurchaseOrderRequest("Restock", List.of(detail));

        when(poService.create(request, "admin")).thenReturn(detailResponse);

        ResponseEntity<BaseResponse<PODetailWithHeaderResponse>> response = purchaseController.create(requestContext, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

    }

    @Test
    void update_ShouldReturn200() {

        PurchaseOrderDetailRequest detail = new PurchaseOrderDetailRequest(1, 10);
        PurchaseOrderRequest request = new PurchaseOrderRequest("Restock Update", List.of(detail));

        when(poService.update(10, request, "admin")).thenReturn(detailResponse);

        ResponseEntity<BaseResponse<PODetailWithHeaderResponse>> response = purchaseController.update(requestContext, 10, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void delete_ShouldReturn200() {

        doNothing().when(poService).delete(10);

        ResponseEntity<BaseResponse<Void>> response = purchaseController.delete(requestContext, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void getById_WhenNotFound_ShouldThrowException() {

        when(poService.findById(999)).thenThrow(new ResourceNotFoundException("PO not found"));

        assertThatThrownBy(() -> purchaseController.getPurchaseOrderByIdPath(requestContext, 999))
                .isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void create_WhenItemNotFound_ShouldThrowException() {

        PurchaseOrderDetailRequest detail = new PurchaseOrderDetailRequest(99, 5);
        PurchaseOrderRequest request = new PurchaseOrderRequest("Restock", List.of(detail));
        when(poService.create(request, "admin")).thenThrow(new ResourceNotFoundException("Item not found"));

        assertThatThrownBy(() -> purchaseController.create(requestContext, request))
                .isInstanceOf(ResourceNotFoundException.class);

    }
}