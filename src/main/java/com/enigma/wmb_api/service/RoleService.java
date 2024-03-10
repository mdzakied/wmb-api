package com.enigma.wmb_api.service;

import com.enigma.wmb_api.constant.UserRoleEnum;
import com.enigma.wmb_api.entity.Role;

public interface RoleService {
    Role getOrSave(UserRoleEnum role);
}
