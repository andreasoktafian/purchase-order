package andreas.purchaseorder.controller;

import andreas.purchaseorder.dto.request.user.UserCreateRequest;
import andreas.purchaseorder.dto.request.user.UserUpdateRequest;
import andreas.purchaseorder.dto.response.UserResponse;
import andreas.purchaseorder.service.UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(UserController.class)
class UserControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private UserService userService;
//
//    @Test
//    void getUsers_ShouldReturnPageOfUsers() throws Exception {
//        UserResponse responseDto = new UserResponse(1, "Andreas", "Oktafian", "andreas@example.com", "08123456789");
//        Page<UserResponse> page = new PageImpl<>(List.of(responseDto));
//
//        when(userService.findAll(any(Pageable.class))).thenReturn(page);
//
//        mockMvc.perform(get("/api/users")
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("All users retrieved successfully"))
//                .andExpect(jsonPath("$.data.content[0].firstName").value("Andreas"));
//
//        verify(userService, times(1)).findAll(any(Pageable.class));
//    }
//
//    @Test
//    void getUserById_ShouldReturnUser() throws Exception {
//        UserResponse responseDto = new UserResponse(1, "Andreas", "Oktafian", "andreas@example.com", "08123456789");
//
//        when(userService.findById(1)).thenReturn(responseDto);
//
//        mockMvc.perform(get("/api/users")
//                        .param("id", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
//                .andExpect(jsonPath("$.data.firstName").value("Andreas"));
//
//        verify(userService, times(1)).findById(1);
//    }
//
//    @Test
//    void createUser_ShouldReturnCreated() throws Exception {
//        UserCreateRequest request = new UserCreateRequest("Andreas", "Oktafian", "andreas@example.com", "08123456789");
//        UserResponse responseDto = new UserResponse(1, "Andreas", "Oktafian", "andreas@example.com", "08123456789");
//
//        when(userService.create(any(UserCreateRequest.class), any())).thenReturn(responseDto);
//
//        mockMvc.perform(post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(header().exists("Location"))
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("User created successfully"))
//                .andExpect(jsonPath("$.data.firstName").value("Andreas"));
//
//        verify(userService, times(1)).create(any(UserCreateRequest.class), any());
//    }
//
//    @Test
//    void updateUser_ShouldReturnUpdatedUser() throws Exception {
//        UserUpdateRequest request = new UserUpdateRequest("Updated", "Name", "updated@example.com", "08123456789");
//        UserResponse responseDto = new UserResponse(1, "Updated", "Name", "updated@example.com", "08123456789");
//
//        when(userService.update(eq(1), any(UserUpdateRequest.class), any())).thenReturn(responseDto);
//
//        mockMvc.perform(put("/api/users/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("User updated successfully"))
//                .andExpect(jsonPath("$.data.firstName").value("Updated"));
//
//        verify(userService, times(1)).update(eq(1), any(UserUpdateRequest.class), any());
//    }
//
//    @Test
//    void deleteUser_ShouldReturnSuccess() throws Exception {
//        doNothing().when(userService).delete(1);
//
//        mockMvc.perform(delete("/api/users/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("User deleted successfully"));
//
//        verify(userService, times(1)).delete(1);
//    }
}