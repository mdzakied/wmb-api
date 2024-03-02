package com.enigma.wmb_api.service;

// import com.enigma.wmb_api.dto.request.user.PostUserRequest;
import com.enigma.wmb_api.dto.request.user.PutUserRequest;
import com.enigma.wmb_api.dto.request.user.SearchUserRequest;
import com.enigma.wmb_api.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    // User Created from Register User Account
    // User create(PostUserRequest postUserRequest);
    Page<User> getAll(SearchUserRequest searchUserRequest);
    User getById(String id);
    User update(PutUserRequest putUserRequest);
    void deleteById(String id);
}
