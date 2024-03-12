package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.UserRoleEnum;
import com.enigma.wmb_api.dto.request.user.PutUserRequest;
import com.enigma.wmb_api.dto.request.user.SearchUserRequest;
import com.enigma.wmb_api.dto.response.user.UserResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repositry.UserRepository;
import com.enigma.wmb_api.service.UserAccountService;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private UserAccountService userAccountService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, validationUtil, userAccountService);
    }

    @Test
    void shouldReturnUserWhenCreate() {
        // Given User Account
        UserAccount userAccount = UserAccount.builder()
                .id("ua-01")
                .username("Dzaki")
                .password("password")
                .role(List.of(
                        Role.builder()
                                .id("r-01")
                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                .build()
                ))
                .build();

        // Given User
        User user = User.builder()
                .name(userAccount.getUsername())
                .phoneNumber("0881")
                .status(true)
                .userAccount(userAccount)
                .build();

        // Stubbing Config save user
        Mockito.when(userRepository.saveAndFlush(Mockito.any(User.class)))
                .thenReturn(user);

        // When
        User actualUser = userService.create(user);

        // Then
        assertEquals(user.getName(), actualUser.getName());
    }

    @Test
    void shouldReturnPageableUserResponseWhenGetAll() {
        // Given User Account
        UserAccount userAccount = UserAccount.builder()
                .id("ua-01")
                .username("Dzaki")
                .password("password")
                .role(List.of(
                        Role.builder()
                                .id("r-01")
                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                .build()
                ))
                .build();

        // Given User
        User user = User.builder()
                .name(userAccount.getUsername())
                .phoneNumber("0881")
                .status(true)
                .userAccount(userAccount)
                .build();

        // Given List User
        List<User> users = List.of(
                user
        );

        // Given Pageable from Menus
        Pageable pageable = PageRequest.of(1, 1);
        Page<User> userPages = new PageImpl<>(users, pageable, users.size());

        // Stubbing Config find all user
        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(userPages);

        // When
        Page<UserResponse> actualUser = userService.getAll(SearchUserRequest.builder()
                .page(1)
                .size(10)
                .sortBy("name")
                .direction("asc")
                .build());

        // Then
        assertEquals(userPages.getTotalElements(), actualUser.getTotalElements());
    }

    @Test
    void shouldReturnUserResponseWhenGetOneById() {
        // Given Param
        String id = "u-01";

        // Given User Account
        UserAccount userAccount = UserAccount.builder()
                .id("ua-01")
                .username("Dzaki")
                .password("password")
                .role(List.of(
                        Role.builder()
                                .id("r-01")
                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                .build()
                ))
                .build();

        // Given User
        User user = User.builder()
                .id("u-01")
                .name(userAccount.getUsername())
                .phoneNumber("0881")
                .status(true)
                .userAccount(userAccount)
                .build();

        // Stubbing Config find by id user
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        // When
        UserResponse actualUser = userService.getOneById(id);

        // Then
        assertEquals(id, actualUser.getId());
    }

    @Test
    void shouldReturnUserWhenGetById() {
        // Given Param
        String id = "u-01";

        // Given User Account
        UserAccount userAccount = UserAccount.builder()
                .id("ua-01")
                .username("Dzaki")
                .password("password")
                .role(List.of(
                        Role.builder()
                                .id("r-01")
                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                .build()
                ))
                .build();

        // Given User
        User user = User.builder()
                .id("u-01")
                .name(userAccount.getUsername())
                .phoneNumber("0881")
                .status(true)
                .userAccount(userAccount)
                .build();

        // Stubbing Config find by id user
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        // When
        User actualUser = userService.getById(id);

        // Then
        assertEquals(id, actualUser.getId());
    }

    @Test
    void shouldReturnUserResponseWhenUpdate() {
        // Given Param
        String id = "u-01";

        // Given Parameter Request
        PutUserRequest parameterUser = PutUserRequest.builder()
                .id(id)
                .name("M Dzaki E D")
                .phoneNumber("08812")
                .build();

        // Given User Account
        UserAccount userAccount = UserAccount.builder()
                .id("ua-01")
                .username("Dzaki")
                .password("password")
                .role(List.of(
                        Role.builder()
                                .id("r-01")
                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                .build()
                ))
                .build();

        // Given User
        User user = User.builder()
                .id("u-01")
                .name("Dzaki")
                .phoneNumber("0881")
                .status(true)
                .userAccount(userAccount)
                .build();

        // Stubbing Config find user
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        // Set Menu
        user.setName(parameterUser.getName());
        user.setPhoneNumber(parameterUser.getPhoneNumber());

        // Stubbing Config save user
        Mockito.when(userRepository.saveAndFlush(Mockito.any(User.class)))
                .thenReturn(user);

        // When
        UserResponse actualUser = userService.update(parameterUser);

        // Then
        assertEquals(user.getName(), actualUser.getName());
    }

    @Test
    void shouldDeleteById() {
        // Given Param
        String id = "u-01";

        // Given User Account
        UserAccount userAccount = UserAccount.builder()
                .id("ua-01")
                .username("Dzaki")
                .password("password")
                .role(List.of(
                        Role.builder()
                                .id("r-01")
                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                .build()
                ))
                .build();

        // Given User
        User user = User.builder()
                .id("u-01")
                .name(userAccount.getUsername())
                .phoneNumber("0881")
                .status(true)
                .userAccount(userAccount)
                .build();

        // Stubbing Config find by id user
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        // Soft Delete

        // Stubbing Config save user
        Mockito.when(userRepository.saveAndFlush(Mockito.any(User.class)))
                .thenReturn(user);

        // When
        userService.deleteById(id);

        // Then
        assertEquals(false, user.getStatus());
    }

    @Test
    void shouldReturnUserWhenFindByIdOrThrowNotFound() {
        // Given Param
        String id = "u-01";

        // Given User Account
        UserAccount userAccount = UserAccount.builder()
                .id("ua-01")
                .username("Dzaki")
                .password("password")
                .role(List.of(
                        Role.builder()
                                .id("r-01")
                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                .build()
                ))
                .build();

        // Given User
        User user = User.builder()
                .id("u-01")
                .name(userAccount.getUsername())
                .phoneNumber("0881")
                .status(true)
                .userAccount(userAccount)
                .build();

        // Stubbing Config find by id user
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        // When
        User actualUser = userService.getById(id);

        // Then
        assertEquals(id, actualUser.getId());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenGetById(){
        assertThrows(RuntimeException.class, () -> {
            userService.getById("Random Id");
        });
    }

    @Test
    void hasAuthoritySelf() {
    }

    @Test
    void testHasAuthoritySelf() {
    }
}