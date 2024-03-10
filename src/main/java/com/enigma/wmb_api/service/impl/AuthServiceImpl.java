package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.UserRoleEnum;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.auth.LoginResponse;
import com.enigma.wmb_api.dto.response.auth.RegisterResponse;
import com.enigma.wmb_api.entity.Role;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repositry.UserAccountRepository;
import com.enigma.wmb_api.repositry.UserRepository;
import com.enigma.wmb_api.service.AuthService;
import com.enigma.wmb_api.service.JwtService;
import com.enigma.wmb_api.service.RoleService;
import com.enigma.wmb_api.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

        // Create All Role for Superadmin from service or create
        Role superAdminRole = roleService.getOrSave(UserRoleEnum.ROLE_SUPER_ADMIN);
        Role adminRole = roleService.getOrSave(UserRoleEnum.ROLE_ADMIN);
        Role customerRole = roleService.getOrSave(UserRoleEnum.ROLE_CUSTOMER);

        // Create User Account for Superadmin
        UserAccount account = UserAccount.builder()
                .username(superAdminUsername)
                // Encode Password
                .password(passwordEncoder.encode(superAdminPassword))
                .role(List.of(superAdminRole, adminRole, customerRole))
                .isEnable(true)
                .build();

        // Save to Repository
        userAccountRepository.save(account);
    }

    // Create Account User
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse register(AuthRequest request) {
        // Validation request
        validationUtil.validate(request);

        // Get role for customer from service or create
        Role customerRole = roleService.getOrSave(UserRoleEnum.ROLE_CUSTOMER);

        // Encode Password
        String hashPassword = passwordEncoder.encode(request.getPassword());

        // Create User Account
        UserAccount account = UserAccount.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .role(List.of(customerRole))
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

    // Create Account Admin
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse registerAdmin(AuthRequest request) {
        // Validation request
        validationUtil.validate(request);

        // Get role for admin from service or create
        Role adminRole = roleService.getOrSave(UserRoleEnum.ROLE_ADMIN);
        Role customerRole = roleService.getOrSave(UserRoleEnum.ROLE_CUSTOMER);

        // Encode Password
        String hashPassword = passwordEncoder.encode(request.getPassword());

        // Create User Account
        UserAccount account = UserAccount.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .role(List.of(adminRole, customerRole))
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

    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(AuthRequest request) {
        // Create UsernamePasswordAuthenticationToken from request for Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
          request.getUsername(),
          request.getPassword()
        );

        // Authenticate UsernamePasswordAuthenticationToken (Check)
        Authentication authenticate = authenticationManager.authenticate(authentication);

        // Set data authenticate to SecurityContextHolder (save data user in security context holder)
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        // Create User Account from getPrincipal authenticate (get data authenticate user)
        UserAccount userAccount = (UserAccount) authenticate.getPrincipal();

        // Create Token JWT from User Account date
        String token = jwtService.generateToken(userAccount);

        // Login Response
        return LoginResponse.builder()
                .username(userAccount.getUsername())
                .roles(userAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .token(token)
                .build();
    }
}
