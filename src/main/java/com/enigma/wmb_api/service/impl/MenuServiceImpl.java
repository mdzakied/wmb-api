package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.menu.PostMenuRequest;
import com.enigma.wmb_api.dto.request.menu.PutMenuRequest;
import com.enigma.wmb_api.dto.request.menu.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.ImageResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repositry.MenuRepository;
import com.enigma.wmb_api.service.ImageService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.specification.MenuSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final ValidationUtil validationUtil;
    private final ImageService imageService;

    // Create Menu Service
    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuResponse create(PostMenuRequest postMenuRequest) {
        // Validate postMenuRequest
        validationUtil.validate(postMenuRequest);

        // Conditional Image is Empty
        if (postMenuRequest.getImage().isEmpty() || postMenuRequest.getImage() == null) throw new ConstraintViolationException("image is required", null);

        // Image Response from Service
        Image image = imageService.create(postMenuRequest.getImage());

        // Create Menu
        Menu menu = Menu.builder()
                .name(postMenuRequest.getName())
                .price(postMenuRequest.getPrice())
                .image(image)
                .build();

        // Save to Repository
        menuRepository.saveAndFlush(menu);

        // Convert to Menu Response
        return convertToMenuResponse(menu);
    }

    // Get All Menu Service
    @Transactional(readOnly = true)
    @Override
    public Page<MenuResponse> getAll(SearchMenuRequest searchMenuRequest) {
        // Validate Page
        if (searchMenuRequest.getPage() <= 0) searchMenuRequest.setPage(1);
        // Sort
        Sort sort = Sort.by(Sort.Direction.fromString(searchMenuRequest.getDirection()), searchMenuRequest.getSortBy());
        // Pageable
        Pageable pageable = PageRequest.of(searchMenuRequest.getPage() - 1, searchMenuRequest.getSize(), sort);
        // Specification
        Specification<Menu> specification = MenuSpecification.getSpecification(searchMenuRequest);

        // Find All Menu with Pageable
        Page<Menu> menuPages = menuRepository.findAll(specification, pageable);

        // Response Page
        // Convert to Menu Response
        return menuPages.map(this::convertToMenuResponse);
    }

    // Get Menu By Id Service (return menuResponse)
    @Transactional(readOnly = true)
    @Override
    public MenuResponse getOneById(String id) {
        // Find by Id
        Menu menu = findByIdOrThrowNotFound(id);

        // Convert to Menu Response
        return convertToMenuResponse(menu);
    }

    // Get Menu By Id Service (return menu)
    @Transactional(readOnly = true)
    @Override
    public Menu getById(String id) {
        // Find By id (return entity)
        return findByIdOrThrowNotFound(id);
    }

    // Update Menu Service
    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuResponse update(PutMenuRequest putMenuRequest) {
        // Validate putMenuRequest
        validationUtil.validate(putMenuRequest);

        // Get by Id
        Menu currentMenu = getById(putMenuRequest.getId());

        // Conditional Edit Menu -> Image
        Image image = currentMenu.getImage();
        if (putMenuRequest.getImage() != null && !putMenuRequest.getImage().isEmpty()) {
            // Create Image
            image = imageService.create(putMenuRequest.getImage());

            // Delete Current Image
            imageService.deleteById(currentMenu.getImage().getId());
        }

        // Create Menu
        Menu menu = Menu.builder()
                .id(currentMenu.getId())
                .name(putMenuRequest.getName())
                .price(putMenuRequest.getPrice())
                .image(image)
                .build();

        // Save to Repository
        menuRepository.saveAndFlush(menu);

        // Convert to Menu Response
        return convertToMenuResponse(menu);
    }

    // Delete Menu Service
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        // Get by Id
        Menu menu = getById(id);

        // Delete to Repository
        menuRepository.delete(menu);
    }

    // Find Menu or Throw Error Service
    @Transactional(readOnly = true)
    public Menu findByIdOrThrowNotFound(String id) {
        // Find By id throw error
        return menuRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ResponseMessage.ERROR_NOT_FOUND
                )
        );
    }

    // Convert to Response Menu Service
    public MenuResponse convertToMenuResponse(Menu menu) {
        if (menu.getImage() != null){
            return MenuResponse.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .price(menu.getPrice())
                    .image(ImageResponse.builder()
                            .url(APIUrl.PRODUCT_IMAGE_DOWNLOAD_API + menu.getImage().getId())
                            .name(menu.getImage().getName())
                            .build())
                    .build();
        } else {
            return MenuResponse.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .price(menu.getPrice())
                    .build();
        }
    }
}
