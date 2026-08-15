package andreas.purchaseorder.service;

import andreas.purchaseorder.annotation.LogBusinessEvent;
import andreas.purchaseorder.dto.request.user.UserCreateRequest;
import andreas.purchaseorder.dto.request.user.UserUpdateRequest;
import andreas.purchaseorder.dto.response.UserResponse;
import andreas.purchaseorder.entity.User;
import andreas.purchaseorder.exception.customException.ConflictException;
import andreas.purchaseorder.exception.customException.ResourceNotFoundException;
import andreas.purchaseorder.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @LogBusinessEvent("FIND_ALL_USERS")
    public Page<UserResponse> findAll(Pageable pageable) {

        return userRepository.findAll(pageable).map(UserResponse::fromEntity);

    }

    @LogBusinessEvent("FIND_USER_BY_ID")
    public UserResponse findById(Integer id) {

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " could not be found"));

        return UserResponse.fromEntity(user);

    }

    @Transactional
    @LogBusinessEvent("CREATE_USER")
    public UserResponse create(@NonNull UserCreateRequest request, String actionBy) {

        validateNewUser(request);

        var user = User.builder()
                .id(generateUniqueRandomId())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .createdBy(actionBy)
                .build();

        return UserResponse.fromEntity(userRepository.save(user));

    }

    @Transactional
    @LogBusinessEvent("UPDATE_USER")
    public UserResponse update(Integer id, @NonNull UserUpdateRequest request, String actionBy) {

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " could not be found"));

        validateExistingUsers(request, user);
        applyUserUpdates(request, user, actionBy);

        return UserResponse.fromEntity(userRepository.save(user));

    }

    @Transactional
    @LogBusinessEvent("DELETE_USER")
    public void delete(Integer id) {

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " could not be found"));

        userRepository.delete(user);

    }

    private Integer generateUniqueRandomId() {
        Integer randomId;
        boolean isDuplicate;

        do {
            randomId = ThreadLocalRandom.current().nextInt(10_000, 1_999_999_999);

            isDuplicate = userRepository.existsById(randomId);

        } while (isDuplicate);

        return randomId;
    }

    private void validateNewUser(@NonNull UserCreateRequest request) {

        if (StringUtils.hasText(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email " + request.email() + " is already in use");
        }

        if (StringUtils.hasText(request.phone()) && userRepository.existsByPhone(request.phone())) {
            throw new ConflictException("Phone number " + request.phone() + " is already in use");
        }

    }

    private void validateExistingUsers(@NonNull UserUpdateRequest request, User user) {

        if (StringUtils.hasText(request.email())
                && !request.email().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmailAndIdNot(request.email(), user.getId())) {
            throw new ConflictException("Email " + request.email() + " is already in use");
        }

        if (StringUtils.hasText(request.phone())
                && !request.phone().equals(user.getPhone())
                && userRepository.existsByPhoneAndIdNot(request.phone(), user.getId())) {
            throw new ConflictException("Phone number " + request.phone() + " is already in use");
        }

    }

    private void applyUserUpdates(@NonNull UserUpdateRequest request, User user, String actionBy) {

        boolean isModified = false;

        if (StringUtils.hasText(request.firstName())) {
            user.setFirstName(request.firstName());
            isModified = true;
        }

        if (StringUtils.hasText(request.lastName())) {
            user.setLastName(request.lastName());
            isModified = true;
        }

        if (StringUtils.hasText(request.email())) {
            user.setEmail(request.email());
            isModified = true;
        }

        if (StringUtils.hasText(request.phone())) {
            user.setPhone(request.phone());
            isModified = true;
        }

        if (isModified) {
            user.setUpdatedBy(actionBy);
        }

    }

}
