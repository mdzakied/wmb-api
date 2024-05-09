package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.auth.LoginResponse;
import com.enigma.wmb_api.dto.response.auth.RegisterResponse;

public interface AuthService {
    RegisterResponse register(AuthRequest request);
    RegisterResponse registerAdmin(AuthRequest request);
    LoginResponse login(AuthRequest request);
    boolean validateToken();
}
