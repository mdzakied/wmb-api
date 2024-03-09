package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.menu.PostMenuRequest;
import com.enigma.wmb_api.dto.request.menu.PutMenuRequest;
import com.enigma.wmb_api.dto.request.menu.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Menu;
import org.springframework.data.domain.Page;

public interface MenuService {
    MenuResponse create(PostMenuRequest postMenuRequest);
    Page<MenuResponse> getAll(SearchMenuRequest searchMenuRequest);
    MenuResponse getOneById(String id);
    Menu getById(String id);
    MenuResponse update(PutMenuRequest putMenuRequest);
    void deleteById(String id);
}
