package andreas.purchaseorder.controller;

import andreas.purchaseorder.dto.context.AppRequestContext;
import andreas.purchaseorder.dto.request.user.UserCreateRequest;
import andreas.purchaseorder.dto.request.user.UserUpdateRequest;
import andreas.purchaseorder.dto.response.BaseResponse;
import andreas.purchaseorder.dto.response.UserResponse;
import andreas.purchaseorder.exception.customException.ConflictException;
import andreas.purchaseorder.exception.customException.ResourceNotFoundException;
import andreas.purchaseorder.service.UserService;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse userResponse;
    private AppRequestContext requestContext;

    @BeforeEach
    void setUp() {
        requestContext = new AppRequestContext("admin", "test-corr-id");
        userResponse = new UserResponse(1001, "Andreas", "Oktafian", "andreas@test.com", "081234", "admin", null, null, null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getUsers_ShouldReturn200() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponse> page = new PageImpl<>(List.of(userResponse));
        when(userService.findAll(pageable)).thenReturn(page);

        ResponseEntity<BaseResponse<Page<UserResponse>>> response = userController.getUsers(requestContext, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert response.getBody() != null;
        assertThat(response.getBody().data().getContent()).hasSize(1);

    }

    @Test
    void getUserByIdParam_ShouldReturn200() {

        when(userService.findById(1001)).thenReturn(userResponse);

        ResponseEntity<BaseResponse<UserResponse>> response = userController.getUserByIdParam(requestContext, 1001);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert response.getBody() != null;
        assertThat(response.getBody().data().firstName()).isEqualTo("Andreas");

    }

    @Test
    void getUserByIdPath_ShouldReturn200() {

        when(userService.findById(1001)).thenReturn(userResponse);

        ResponseEntity<BaseResponse<UserResponse>> response = userController.getUserByIdPath(requestContext, 1001);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void create_ShouldReturn201() {

        UserCreateRequest request = new UserCreateRequest("Andreas", "Oktafian", "andreas@test.com", "081234");
        when(userService.create(request, "admin")).thenReturn(userResponse);

        ResponseEntity<BaseResponse<UserResponse>> response = userController.create(requestContext, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

    }

    @Test
    void update_ShouldReturn200() {

        UserUpdateRequest request = new UserUpdateRequest("Andreas Updated", null, null, null);
        when(userService.update(1001, request, "admin")).thenReturn(userResponse);

        ResponseEntity<BaseResponse<UserResponse>> response = userController.update(requestContext, 1001, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void delete_ShouldReturn200() {

        doNothing().when(userService).delete(1001);

        ResponseEntity<BaseResponse<Void>> response = userController.delete(requestContext, 1001);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).delete(1001);

    }

    @Test
    void getUserById_WhenNotFound_ShouldThrowException() {

        when(userService.findById(999)).thenThrow(new ResourceNotFoundException("User not found"));

        assertThatThrownBy(() -> userController.getUserByIdPath(requestContext, 999))
                .isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void create_WhenEmailConflict_ShouldThrowException() {

        UserCreateRequest request = new UserCreateRequest("John", "Doe", "exist@test.com", "081234");
        when(userService.create(request, "admin")).thenThrow(new ConflictException("Email already in use"));

        assertThatThrownBy(() -> userController.create(requestContext, request))
                .isInstanceOf(ConflictException.class);

    }

    @Test
    void delete_WhenNotFound_ShouldThrowException() {

        doThrow(new ResourceNotFoundException("User not found")).when(userService).delete(999);

        assertThatThrownBy(() -> userController.delete(requestContext, 999))
                .isInstanceOf(ResourceNotFoundException.class);

    }
}