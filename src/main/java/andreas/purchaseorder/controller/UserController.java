package andreas.purchaseorder.controller;

import andreas.purchaseorder.dto.context.AppRequestContext;
import andreas.purchaseorder.dto.request.user.UserCreateRequest;
import andreas.purchaseorder.dto.request.user.UserUpdateRequest;
import andreas.purchaseorder.dto.response.BaseResponse;
import andreas.purchaseorder.dto.response.UserResponse;
import andreas.purchaseorder.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<UserResponse>>> getUsers(
            AppRequestContext context,
            Pageable pageable) {

        var users = userService.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.success(users, "All users retrieved successfully"));

    }

    @GetMapping(params = "id")
    public ResponseEntity<BaseResponse<UserResponse>> getUserByIdParam(
            AppRequestContext context,
            @RequestParam Integer id) {

        var user = userService.findById(id);
        return ResponseEntity.ok(BaseResponse.success(user, "User retrieved successfully"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> getUserByIdPath(
            AppRequestContext context,
            @PathVariable Integer id) {

        var user = userService.findById(id);
        return ResponseEntity.ok(BaseResponse.success(user, "User retrieved successfully"));

    }

    @PostMapping
    public ResponseEntity<BaseResponse<UserResponse>> create(
            AppRequestContext context,
            @Valid @RequestBody UserCreateRequest request) {

        var user = userService.create(request, context.actionBy());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .queryParam("id", user.id())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(BaseResponse.success(user, "User created successfully"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> update(
            AppRequestContext context,
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdateRequest request) {

        var user = userService.update(id, request, context.actionBy());
        return ResponseEntity.ok(BaseResponse.success(user, "User updated successfully"));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            AppRequestContext context,
            @PathVariable Integer id) {

        userService.delete(id);
        return ResponseEntity.ok(BaseResponse.success(null, "User deleted successfully"));

    }

}
