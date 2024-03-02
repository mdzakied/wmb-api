package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.menu.PostMenuRequest;
import com.enigma.wmb_api.dto.request.menu.PutMenuRequest;
import com.enigma.wmb_api.dto.request.menu.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repositry.MenuRepository;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.specification.MenuSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final ValidationUtil validationUtil;

    @Override
    public Menu create(PostMenuRequest postMenuRequest) {
        // Validate postMenuRequest
        validationUtil.validate(postMenuRequest);

        Menu menu = Menu.builder()
                .name(postMenuRequest.getName())
                .price(postMenuRequest.getPrice())
                .build();

        return menuRepository.saveAndFlush(menu);
    }

    @Override
    public Page<Menu> getAll(SearchMenuRequest searchMenuRequest) {
        // Validate Page
        if (searchMenuRequest.getPage() <=0) searchMenuRequest.setPage(1);
        // Sort
        Sort sort = Sort.by(Sort.Direction.fromString(searchMenuRequest.getDirection()), searchMenuRequest.getSortBy());
        // Pageable
        Pageable pageable = PageRequest.of(searchMenuRequest.getPage() - 1, searchMenuRequest.getSize(), sort);
        // Specification
        Specification<Menu> specification = MenuSpecification.getSpecification(searchMenuRequest);

        return menuRepository.findAll(specification, pageable);
    }

    @Override
    public Menu getById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    @Override
    public Menu update(PutMenuRequest putMenuRequest) {
        // Validate putMenuRequest
        validationUtil.validate(putMenuRequest);

        // Get by Id
        Menu menu = getById(putMenuRequest.getId());

        Menu editedMenu = Menu.builder()
                .id(menu.getId())
                .name(putMenuRequest.getName())
                .price(putMenuRequest.getPrice())
                .build();

        return menuRepository.saveAndFlush(editedMenu);
    }

    @Override
    public void deleteById(String id) {
        // Get by Id
        Menu menu = getById(id);

        menuRepository.delete(menu);
    }

    public Menu findByIdOrThrowNotFound(String id){
        return menuRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "menu not found"
            )
        );
    }
}
