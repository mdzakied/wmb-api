package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repositry.UserAccountRepository;
import com.enigma.wmb_api.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find by username from repository
        return userAccountRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("username not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public UserAccount getByUserId(String id) {
        // Find by id from repository
        return userAccountRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public UserAccount getByContext() {
        // Get authentication from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Find by username from repository
        return userAccountRepository.findByUsername(authentication.getPrincipal().toString())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_USER_NOT_FOUND));
    }
}
