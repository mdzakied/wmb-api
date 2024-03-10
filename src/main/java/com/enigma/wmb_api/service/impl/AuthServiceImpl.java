package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.UserRoleEnum;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.auth.RegisterResponse;
import com.enigma.wmb_api.entity.Role;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repositry.UserAccountRepository;
import com.enigma.wmb_api.repositry.UserRepository;
import com.enigma.wmb_api.service.AuthService;
import com.enigma.wmb_api.service.RoleService;
import com.enigma.wmb_api.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserAccountRepository userAccountRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;

    // Dependency Inject from environment
    @Value("${enigma_shop.username.superadmin}")
    private String superAdminUsername;
    @Value("${enigma_shop.password.superadmin}")
    private String superAdminPassword;

    // Auto Create Account SuperAdmin
    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initSuperAdmin() {
        // Conditional Check Superadmin Account for create
        Optional<UserAccount> currentUser = userAccountRepository.findByUsername(superAdminUsername);
        if (currentUser.isPresent()) return;

        // Create All Role for Superadmin and Check first to create
        Role superAdmin = roleService.getOrSave(UserRoleEnum.ROLE_SUPER_ADMIN);
        Role admin = roleService.getOrSave(UserRoleEnum.ROLE_ADMIN);
        Role customer = roleService.getOrSave(UserRoleEnum.ROLE_CUSTOMER);

        // Create User Account for Superadmin
        UserAccount account = UserAccount.builder()
                .username(superAdminUsername)
                // Encode Password
                .password(passwordEncoder.encode(superAdminPassword))
                .role(List.of(superAdmin, admin, customer))
                .isEnable(true)
                .build();

        // Save to Repository
        userAccountRepository.save(account);
    }
    @Override
    public RegisterResponse register(AuthRequest request) {
        // Validation request
        validationUtil.validate(request);

        // Get or Save role customer from service
        Role role = roleService.getOrSave(UserRoleEnum.ROLE_CUSTOMER);

        // Encode Password
        String hashPassword = passwordEncoder.encode(request.getPassword());

        // Create User Account
        UserAccount account = UserAccount.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .role(List.of(role))
                .isEnable(true)
                .build();

        // Save to Repository
        userAccountRepository.saveAndFlush(account);

        // Create User
        User user = User.builder()
                .name(account.getUsername())
                .userAccount(account)
                .status(true)
                .build();

        // Save to Repository
        userRepository.saveAndFlush(user);

        // Role Register Response
        List<String> roles = account.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).toList();

        // Register Response
        return RegisterResponse.builder()
                .username(account.getUsername())
                .roles(roles)
                .build();
    }
}
