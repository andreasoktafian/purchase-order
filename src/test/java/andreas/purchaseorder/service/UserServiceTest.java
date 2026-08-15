package andreas.purchaseorder.service;

import andreas.purchaseorder.dto.request.user.UserCreateRequest;
import andreas.purchaseorder.dto.request.user.UserUpdateRequest;
import andreas.purchaseorder.dto.response.UserResponse;
import andreas.purchaseorder.entity.User;
import andreas.purchaseorder.exception.customException.ConflictException;
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
                .id(1001)
                .firstName("Andreas")
                .lastName("Oktafian")
                .email("andreas@example.com")
                .phone("08123456789")
                .createdBy("admin")
                .build();
    }

    @Test
    void findAll_ShouldReturnPagedUsers() {

        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user)));

        Page<UserResponse> result = userService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().firstName()).isEqualTo("Andreas");

    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {

        when(userRepository.findById(1001)).thenReturn(Optional.of(user));
        UserResponse result = userService.findById(1001);
        assertThat(result.firstName()).isEqualTo("Andreas");

    }

    @Test
    void create_WhenValidRequest_ShouldSaveAndReturnUser() {

        UserCreateRequest request = new UserCreateRequest("John", "Doe", "john@example.com", "0899999");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("0899999")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse result = userService.create(request, "admin");

        assertThat(result.firstName()).isEqualTo("John");
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void update_WhenValidRequest_ShouldUpdateAndReturnUser() {

        UserUpdateRequest request = new UserUpdateRequest("Updated", null, "updated@example.com", null);

        when(userRepository.findById(1001)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("updated@example.com", 1001)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.update(1001, request, "admin");

        assertThat(result.firstName()).isEqualTo("Updated");
        verify(userRepository).save(user);

    }

    @Test
    void delete_WhenUserExists_ShouldDeleteUser() {

        when(userRepository.findById(1001)).thenReturn(Optional.of(user));
        userService.delete(1001);
        verify(userRepository, times(1)).delete(user);

    }

    @Test
    void findById_WhenUserNotFound_ShouldThrowException() {

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("could not be found");

    }

    @Test
    void create_WhenEmailExists_ShouldThrowConflictException() {

        UserCreateRequest request = new UserCreateRequest("John", "Doe", "exist@example.com", "0899999");

        when(userRepository.existsByEmail("exist@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request, "admin"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already in use");

        verify(userRepository, never()).save(any());

    }

    @Test
    void update_WhenPhoneExists_ShouldThrowConflictException() {

        UserUpdateRequest request = new UserUpdateRequest(null, null, null, "08111111");

        when(userRepository.findById(1001)).thenReturn(Optional.of(user));
        when(userRepository.existsByPhoneAndIdNot("08111111", 1001)).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1001, request, "admin"))
                .isInstanceOf(ConflictException.class);

    }

}
