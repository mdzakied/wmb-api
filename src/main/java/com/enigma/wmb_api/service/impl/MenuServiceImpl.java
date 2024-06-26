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

        // Conditional Edit Menu -> Image
        Image image = null;
        if (postMenuRequest.getImage() != null && !postMenuRequest.getImage().isEmpty()) {
            // Create Image
            image = imageService.create(postMenuRequest.getImage());
        }

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
        Image image = null;
        String currentImgId = null;

        // Set Current Menu for Edit without Img
        if (currentMenu.getImage() != null) currentImgId = currentMenu.getImage().getId();

        // Create Image for Edit with Img
        if (putMenuRequest.getImage() != null) {
            // Create Image
            image = imageService.create(putMenuRequest.getImage());
        }

        // Create Menu
        Menu menu = Menu.builder()
                .id(currentMenu.getId())
                .name(putMenuRequest.getName())
                .price(putMenuRequest.getPrice())
                .build();

        // Conditional Edit Image
        if (currentMenu.getImage() != null && putMenuRequest.getImage() == null) {
            menu.setImage(currentMenu.getImage());
        } else {
            menu.setImage(image);
        }

        // Save to Repository
        menuRepository.saveAndFlush(menu);

        // Conditional Replace Image
        if (currentImgId != null && putMenuRequest.getImage() != null) {
            imageService.deleteById(currentImgId);
        }

        // Convert to Menu Response
        return convertToMenuResponse(menu);
    }

    // Delete Menu Service
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        // Get by Id
        Menu currentMenu = getById(id);

        // Conditional Edit Menu -> Image
        if (currentMenu.getImage() != null) {
            // Delete Current Image
            imageService.deleteById(currentMenu.getImage().getId());
        }

        // Delete to Repository
        menuRepository.delete(currentMenu);
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
        // Response Image
        ImageResponse imageResponse = null;
        if (menu.getImage() != null) {
            imageResponse = ImageResponse.builder()
                    .url(APIUrl.PRODUCT_IMAGE_DOWNLOAD_API + menu.getImage().getId())
                    .name(menu.getImage().getName())
                    .build();
        }

        // Menu Response
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .image(imageResponse)
                .build();
    }
}
