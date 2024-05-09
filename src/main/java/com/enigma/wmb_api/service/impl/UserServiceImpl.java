package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.user.PutUserRequest;
import com.enigma.wmb_api.dto.request.user.SearchUserRequest;
import com.enigma.wmb_api.dto.response.user.RoleResponse;
import com.enigma.wmb_api.dto.response.user.UserAccountResponse;
import com.enigma.wmb_api.dto.response.user.UserResponse;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repositry.UserAccountRepository;
import com.enigma.wmb_api.repositry.UserRepository;
import com.enigma.wmb_api.service.UserAccountService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;
    private final UserAccountService userAccountService;

    // -- Create User from Register User Account --
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User create(User user) {
        // Save to Repository
        return userRepository.saveAndFlush(user);
    }

    // Get All User Service
    @Transactional(readOnly = true)
    @Override
    public Page<UserResponse> getAll(SearchUserRequest searchUserRequest) {
        // Validate Page
        if (searchUserRequest.getPage() <=0) searchUserRequest.setPage(1);
        // Sort
        Sort sort = Sort.by(Sort.Direction.fromString(searchUserRequest.getDirection()), searchUserRequest.getSortBy());
        // Pageable
        Pageable pageable = PageRequest.of(searchUserRequest.getPage() - 1, searchUserRequest.getSize(), sort);
        // Specification
        Specification<User> specification = UserSpecification.getSpecification(searchUserRequest);

        // Find All User with Pageable
        Page<User> userPages = userRepository.findAll(specification, pageable);

        // Response Page
        // Convert to User Response
        return userPages.map(this::convertToUserResponse);
    }

    // Get User By Id Service (return userResponse)
    @Transactional(readOnly = true)
    @Override
    public UserResponse getOneById(String id) {
        // Find by Id
        User user = findByIdOrThrowNotFound(id);

        // Convert to User Response
        return convertToUserResponse(user);
    }

    // Get User By Id Service (return user)
    @Transactional(readOnly = true)
    @Override
    public User getById(String id) {
        // Find By id (return entity)
        return findByIdOrThrowNotFound(id);
    }

    // Update User Service
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse update(PutUserRequest putUserRequest) {
        // Validate putUserRequest
        validationUtil.validate(putUserRequest);

        // Get by Id
        User currentUser = getById(putUserRequest.getId());

        // Create User
        User user = User.builder()
                .id(currentUser.getId())
                .name(putUserRequest.getName())
                .phoneNumber(putUserRequest.getPhoneNumber())
                .status(putUserRequest.getStatus())
                .userAccount(currentUser.getUserAccount())
                .build();

        // Save to Repository
        userRepository.saveAndFlush(user);

        // Convert to User Response
        return convertToUserResponse(user);
    }

    // Delete User Service (Soft Delete and Delete User Account)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        // Get by Id
        User user = getById(id);

        // Set status to false
        user.setStatus(false);

        // Save to Repository
        userRepository.saveAndFlush(user);

        // Soft Delete User Account
        userAccountService.deleteById(user.getUserAccount().getId());
    }

    // Find User or Throw Error Service
    @Transactional(readOnly = true)
    public User findByIdOrThrowNotFound(String id){
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ResponseMessage.ERROR_NOT_FOUND
                )
        );
    }

    // Convert to Response User Service
    public UserResponse convertToUserResponse(User user) {

        // Roles User Account Response
        List<RoleResponse> roleResponses = user.getUserAccount().getRole().stream().map(
                roleResponse -> {
                    return RoleResponse.builder()
                            .id(roleResponse.getId())
                            .role(String.valueOf(roleResponse.getRole()))
                            .build();
                }
        ).toList();

        // User Account Response
        UserAccountResponse userAccountResponse = UserAccountResponse.builder()
                .id(user.getUserAccount().getId())
                .username(user.getUserAccount().getUsername())
                .roles(roleResponses)
                .isEnable(user.getUserAccount().getIsEnable())
                .build();

        // User Response
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .userAccount(userAccountResponse)
                .build();
    }

    // Custom method for authorize user getById
    @Transactional(rollbackFor = Exception.class)
    public boolean hasAuthoritySelf(String id) {
        // Find user By Id
        User currentUser = getById(id);

        // Find User Account from getByContext service
        UserAccount userAccount = userAccountService.getByContext();

        // Conditional User Account
        if (!userAccount.getId().equals(currentUser.getUserAccount().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ResponseMessage.ERROR_FORBIDDEN);
        }

        return true;
    }

    // Custom method for authorize user update
    @Transactional(rollbackFor = Exception.class)
    public boolean hasAuthoritySelf(PutUserRequest payload) {
        // Find user By Id
        User currentUser = getById(payload.getId());

        // Find User Account from getByContext service
        UserAccount userAccount = userAccountService.getByContext();

        // Conditional User Account
        if (!userAccount.getId().equals(currentUser.getUserAccount().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ResponseMessage.ERROR_FORBIDDEN);
        }

        return true;
    }
}
