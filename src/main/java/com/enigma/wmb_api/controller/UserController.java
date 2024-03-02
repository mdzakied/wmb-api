package com.enigma.wmb_api.controller;


import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.user.PutUserRequest;
import com.enigma.wmb_api.dto.request.user.SearchUserRequest;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.PagingResponse;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.USER_API)
public class UserController {
    private final UserService userService;

    // Create User from Register User Account

    // Get All User
    @GetMapping
    public ResponseEntity<CommonResponse<List<User>>> getAllUser (
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String soryBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction
    ) {
        // Query Params & Pagination to SearchUserRequest
        SearchUserRequest searchUserRequest = SearchUserRequest.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .page(page)
                .size(size)
                .sortBy(soryBy)
                .direction(direction)
                .build();

        // Get All User to Service
        Page<User> users = userService.getAll(searchUserRequest);

        // Paging Response for Common Response
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(users.getTotalPages())
                .totalElement(users.getTotalElements())
                .page(users.getPageable().getPageNumber() + 1)
                .size(users.getPageable().getPageSize())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .build();

        // Common Response
        CommonResponse<List<User>> response = CommonResponse.<List<User>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success get all user")
                .data(users.getContent())
                .paging(pagingResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Get User by Id
    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<User>> getUserById(
            @PathVariable String id
    ) {
        // Get User by Id to Service
        User user = userService.getById(id);

        // Common Response
        CommonResponse<User> response = CommonResponse.<User>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully get user by id")
                .data(user)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Update Menu
    @PutMapping
    public ResponseEntity<CommonResponse<User>> updateUser(
            @RequestBody PutUserRequest putUserRequest
    ) {
        // Update User to Service
        User user = userService.update(putUserRequest);

        // Common Response
        CommonResponse<User> userCommonResponse = CommonResponse.<User>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully edit user")
                .data(user)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userCommonResponse);
    }

    // Delete User by Id
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<User>> deleteUserById(
            @PathVariable String id
    ) {

        // Delete User to Service
        userService.deleteById(id);

        // Common Response
        CommonResponse<User> userCommonResponse = CommonResponse.<User>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully delete user")
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userCommonResponse);
    }
}
