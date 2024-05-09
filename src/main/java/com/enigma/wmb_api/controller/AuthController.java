package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.auth.LoginResponse;
import com.enigma.wmb_api.dto.response.auth.RegisterResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.ErrorResponse;
import com.enigma.wmb_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = APIUrl.AUTH_API)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // Create Register Controller
    @Operation(summary = "Public")
    @PostMapping(path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> registerUser(
            @RequestBody AuthRequest request
    ) {
        // Register Response from register Service
        RegisterResponse register = authService.register(request);

        // Common Response
        CommonResponse<RegisterResponse> response = CommonResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(ResponseMessage.SUCCESS_SAVE_DATA)
                .data(register)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Create Register Admin Controller
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin")
    @PostMapping(path = "/register/admin",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> registerAdmin(
            @RequestBody AuthRequest request
    ) {
        // Register Response from register Service
        RegisterResponse register = authService.registerAdmin(request);

        // Common Response
        CommonResponse<RegisterResponse> response = CommonResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(ResponseMessage.SUCCESS_SAVE_DATA)
                .data(register)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Login Controller
    @Operation(summary = "Public")
    @PostMapping(path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> login(
            @RequestBody AuthRequest request
    ) {
        // Login Response from login service
        LoginResponse loginResponse = authService.login(request);

        // Common Response
        CommonResponse<LoginResponse> response = CommonResponse.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_LOGIN)
                .data(loginResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "validate-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateToken() {
        boolean valid = authService.validateToken();
        if (valid) {
            CommonResponse<String> response = CommonResponse.<String>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message(ResponseMessage.VALID_JWT_TOKEN)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(String.valueOf(LocalDateTime.now()))
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message(ResponseMessage.INVALID_JWT_TOKEN)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
