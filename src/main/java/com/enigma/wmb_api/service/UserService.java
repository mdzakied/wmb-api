package com.enigma.wmb_api.service;

// import com.enigma.wmb_api.dto.request.user.PostUserRequest;

import com.enigma.wmb_api.dto.request.user.PutUserRequest;
import com.enigma.wmb_api.dto.request.user.SearchUserRequest;
import com.enigma.wmb_api.dto.response.user.UserResponse;
import com.enigma.wmb_api.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    // User Created from Register User Account
    User create(User user);
    Page<UserResponse> getAll(SearchUserRequest searchUserRequest);
    UserResponse getOneById(String id);
    User getById(String id);
    UserResponse update(PutUserRequest putUserRequest);
    void deleteById(String id);
}
