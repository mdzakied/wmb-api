package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.user.PutUserRequest;
import com.enigma.wmb_api.dto.request.user.SearchUserRequest;
import com.enigma.wmb_api.entity.MTable;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.repositry.UserRepository;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.specification.UserSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;

    // Create User from Register User Account

    @Override
    public Page<User> getAll(SearchUserRequest searchUserRequest) {
        // Validate Page
        if (searchUserRequest.getPage() <=0) searchUserRequest.setPage(1);
        // Sort
        Sort sort = Sort.by(Sort.Direction.fromString(searchUserRequest.getDirection()), searchUserRequest.getSortBy());
        // Pageable
        Pageable pageable = PageRequest.of(searchUserRequest.getPage() - 1, searchUserRequest.getSize(), sort);
        // Specification
        Specification<User> specification = UserSpecification.getSpecification(searchUserRequest);

        return userRepository.findAll(specification, pageable);
    }

    @Override
    public User getById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    @Override
    public User update(PutUserRequest putUserRequest) {
        // Validate putUserRequest
        validationUtil.validate(putUserRequest);

        // Get by Id
        User user = getById(putUserRequest.getId());

        User editedUser = User.builder()
                .id(user.getId())
                .name(putUserRequest.getName())
                .phoneNumber(putUserRequest.getPhoneNumber())
                .build();

        return userRepository.saveAndFlush(editedUser);
    }

    @Override
    public void deleteById(String id) {
        // Get by Id
        User user = getById(id);

        userRepository.delete(user);
    }

    public User findByIdOrThrowNotFound(String id){
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "user not found"
                )
        );
    }
}
