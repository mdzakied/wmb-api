package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.menu.PostMenuRequest;
import com.enigma.wmb_api.dto.request.menu.PutMenuRequest;
import com.enigma.wmb_api.dto.request.menu.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import org.springframework.data.domain.Page;

public interface MenuService {
    Menu create(PostMenuRequest postMenuRequest);
    Page<Menu> getAll(SearchMenuRequest searchMenuRequest);
    Menu getById(String id);
    Menu update(PutMenuRequest putMenuRequest);
    void deleteById(String id);
}
