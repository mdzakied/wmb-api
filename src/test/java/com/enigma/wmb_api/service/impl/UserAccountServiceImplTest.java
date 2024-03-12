package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.UserRoleEnum;
import com.enigma.wmb_api.entity.Role;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repositry.UserAccountRepository;
import com.enigma.wmb_api.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    private UserAccountService userAccountService;

    @BeforeEach
    void setUp() {
        userAccountService = new UserAccountServiceImpl(userAccountRepository);
    }

    @Test
    void shouldReturnUserDetailsByLoadUserByUsername() {
        // Given Param
        String username = "Dzaki";

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

        // Stubbing Config find all user
        Mockito.when(userAccountRepository.findByUsername(username))
                .thenReturn(Optional.of(userAccount));

        // When
        UserDetails actualUserDetails = userAccountService.loadUserByUsername(username);

        // Then
        assertEquals(username, actualUserDetails.getUsername());
    }

    @Test
    void shouldReturnUserAccountByGetByUserId() {
        // Given Param
        String id = "ua-01";

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

        // Stubbing Config find all user
        Mockito.when(userAccountRepository.findById(id))
                .thenReturn(Optional.of(userAccount));

        // When
        UserAccount actualUserAccount = userAccountService.getByUserId(id);

        // Then
        assertEquals(id, actualUserAccount.getId());
    }

    @WithMockUser(username = "Dzaki", roles = "CUSTOMER")
    @Test
    void shouldReturnUserAccountByGetByContext() {
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

        // Get authentication from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Stubbing Config find all user
        Mockito.when(userAccountRepository.findByUsername(authentication.getPrincipal().toString()))
                .thenReturn(Optional.of(userAccount));

        // When
        UserAccount actualUserAccount = userAccountService.getByContext();

        // Then
        assertEquals(userAccount.getUsername(), actualUserAccount.getUsername());
    }
}