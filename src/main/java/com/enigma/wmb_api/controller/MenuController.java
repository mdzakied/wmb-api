package com.enigma.wmb_api.controller;


import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.menu.PostMenuRequest;
import com.enigma.wmb_api.dto.request.menu.PutMenuRequest;
import com.enigma.wmb_api.dto.request.menu.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.PagingResponse;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.MENU_API)
public class MenuController {
    private final MenuService menuService;

    // Create Menu
    @PostMapping
    public ResponseEntity<CommonResponse<Menu>> createMenu (
            @RequestBody PostMenuRequest postMenuRequest
    ) {
        // Create Menu to Service
        Menu menu = menuService.create(postMenuRequest);

        // Common Response
        CommonResponse<Menu> menuCommonResponse = CommonResponse.<Menu>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully create menu")
                .data(menu)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(menuCommonResponse);
    }

    // Get All Menu
    @GetMapping
    public ResponseEntity<CommonResponse<List<Menu>>> getAllMenu (
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String soryBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction
    ) {
        // Query Params & Pagination to SearchMenuRequest
        SearchMenuRequest searchMenuRequest = SearchMenuRequest.builder()
                .name(name)
                .page(page)
                .size(size)
                .sortBy(soryBy)
                .direction(direction)
                .build();

        // Get All Menu to Service
        Page<Menu> menus = menuService.getAll(searchMenuRequest);

        // Paging Response for Common Response
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(menus.getTotalPages())
                .totalElement(menus.getTotalElements())
                .page(menus.getPageable().getPageNumber() + 1)
                .size(menus.getPageable().getPageSize())
                .hasNext(menus.hasNext())
                .hasPrevious(menus.hasPrevious())
                .build();

        // Common Response
        CommonResponse<List<Menu>> response = CommonResponse.<List<Menu>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success get all menu")
                .data(menus.getContent())
                .paging(pagingResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<Menu>> getMenuById(
            @PathVariable String id
    ) {
        // Get Menu by Id to Service
        Menu menu = menuService.getById(id);

        // Common Response
        CommonResponse<Menu> response = CommonResponse.<Menu>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully get menu by id")
                .data(menu)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping
    public ResponseEntity<CommonResponse<Menu>> updateMenu(
            @RequestBody PutMenuRequest putMenuRequest
    ) {
        // Edit Menu to Service
        Menu menu = menuService.update(putMenuRequest);

        // Common Response
        CommonResponse<Menu> menuCommonResponse = CommonResponse.<Menu>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully edit menu")
                .data(menu)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(menuCommonResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Menu>> deleteMenuById(
            @PathVariable String id
    ) {

        // Delete Menu to Service
        menuService.deleteById(id);

        // Common Response
        CommonResponse<Menu> menuCommonResponse = CommonResponse.<Menu>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully delete menu")
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(menuCommonResponse);
    }
}
