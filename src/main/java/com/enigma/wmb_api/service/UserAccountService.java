package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAccountService extends UserDetailsService {
    UserAccount getByUserId(String id);
    UserAccount getByContext();
    void deleteById(String id);
}
