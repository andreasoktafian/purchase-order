package andreas.purchaseorder.service;

import andreas.purchaseorder.dto.request.user.UserCreateRequest;
import andreas.purchaseorder.dto.request.user.UserUpdateRequest;
import andreas.purchaseorder.dto.response.UserResponse;
import andreas.purchaseorder.entity.User;
import andreas.purchaseorder.exception.customException.ResourceNotFoundException;
import andreas.purchaseorder.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .firstName("Andreas")
                .lastName("Oktafian")
                .email("andreas@example.com")
                .phone("08123456789")
                .createdBy("system")
                .build();
    }

    @Test
    void findAll_ShouldReturnPagedUsers() {
        Pageable pageable = PageRequest.of(1, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponse> result = userService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().firstName()).isEqualTo("Andreas");
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(1);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("Andreas");
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void findById_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with ID 99 could not be found");

        verify(userRepository, times(1)).findById(99);
    }

    @Test
    void create_ShouldSaveAndReturnUser() {
        UserCreateRequest request = new UserCreateRequest("Andreas", "Oktafian", "andreas@example.com", "08123456789");

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.create(request, "system");

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Andreas");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_WhenUserExists_ShouldUpdateAndReturnUser() {
        UserUpdateRequest request = new UserUpdateRequest("UpdatedName", null, null, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.update(1, request, "admin");

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_WhenUserNotFound_ShouldThrowException() {
        UserUpdateRequest request = new UserUpdateRequest("UpdatedName", null, null, null);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99, request, "admin"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with ID 99 could not be found");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.delete(1);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void delete_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).delete(any(User.class));
    }

}
