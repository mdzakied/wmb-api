package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.menu.PostMenuRequest;
import com.enigma.wmb_api.dto.request.menu.PutMenuRequest;
import com.enigma.wmb_api.dto.request.menu.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponsePage;
import com.enigma.wmb_api.dto.response.common.PagingResponse;
import com.enigma.wmb_api.service.MenuService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.MENU_API)
public class MenuController {
    private final MenuService menuService;
    private final ObjectMapper objectMapper;

    // Create Menu Controller
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<MenuResponse>> createMenu(
            @RequestPart (name = "menu") String jsonMenu,
            @RequestPart (name = "image", required = false) MultipartFile image
    ) {
        // Common Response
        CommonResponse.CommonResponseBuilder<MenuResponse> responseBuilder = CommonResponse.builder();

        try {
            // Object Mapper postMenuRequest
            PostMenuRequest postMenuRequest = objectMapper.readValue(jsonMenu, new TypeReference<>() {});

            // Set Image
            postMenuRequest.setImage(image);

            // Menu Response from crate Service
            MenuResponse menuResponse = menuService.create(postMenuRequest);

            responseBuilder.statusCode(HttpStatus.CREATED.value());
            responseBuilder.message(ResponseMessage.SUCCESS_SAVE_DATA);
            responseBuilder.data(menuResponse);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responseBuilder.build());
        } catch (Exception e) {
            responseBuilder.message(ResponseMessage.ERROR_INTERNAL_SERVER);
            responseBuilder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseBuilder.build());
        }
    }

    // Get All Menu Controller
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponsePage<List<MenuResponse>>> getAllMenu(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "price", required = false) Integer price,
            @RequestParam(name = "minPrice", required = false) Integer minPrice,
            @RequestParam(name = "maxPrice", required = false) Integer maxprice,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String soryBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction
    ) {
        // Query Params & Pagination to SearchMenuRequest
        SearchMenuRequest searchMenuRequest = SearchMenuRequest.builder()
                .name(name)
                .price(price)
                .minPrice(minPrice)
                .maxPrice(maxprice)
                .page(page)
                .size(size)
                .sortBy(soryBy)
                .direction(direction)
                .build();

        // Page All Menu Response from getAll Service
        Page<MenuResponse> menuResponses = menuService.getAll(searchMenuRequest);

        // Paging Response for Common Response
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(menuResponses.getTotalPages())
                .totalElement(menuResponses.getTotalElements())
                .page(menuResponses.getPageable().getPageNumber() + 1)
                .size(menuResponses.getPageable().getPageSize())
                .hasNext(menuResponses.hasNext())
                .hasPrevious(menuResponses.hasPrevious())
                .build();

        // Common Response
        CommonResponsePage<List<MenuResponse>> response = CommonResponsePage.<List<MenuResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(menuResponses .getContent())
                .paging(pagingResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Get Menu by Id Controller
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<MenuResponse>> getMenuById(
            @PathVariable String id
    ) {
        // Menu Response from getOneById service
        MenuResponse menuResponse = menuService.getOneById(id);

        // Common Response
        CommonResponse<MenuResponse> response = CommonResponse.<MenuResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(menuResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Update Menu Controller
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<MenuResponse>> updateMenu(
            @RequestPart (name = "menu") String jsonMenu,
            @RequestPart (name = "image", required = false) MultipartFile image
    ) {
        // Common Response
        CommonResponse.CommonResponseBuilder<MenuResponse> responseBuilder = CommonResponse.builder();

        try {
            // Object Mapper postMenuRequest
            PutMenuRequest putMenuRequest = objectMapper.readValue(jsonMenu, new TypeReference<>() {});

            // Set Image
            putMenuRequest.setImage(image);

            // Menu Response from update Service
            MenuResponse menuResponse = menuService.update(putMenuRequest);

            responseBuilder.statusCode(HttpStatus.CREATED.value());
            responseBuilder.message(ResponseMessage.SUCCESS_UPDATE_DATA);
            responseBuilder.data(menuResponse);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responseBuilder.build());
        } catch (Exception e) {
            responseBuilder.message(ResponseMessage.ERROR_INTERNAL_SERVER);
            responseBuilder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseBuilder.build());
        }
    }

    // Delete Menu Controller
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<MenuResponse>> deleteMenuById(
            @PathVariable String id
    ) {

        // Menu Response from delete Service
        menuService.deleteById(id);

        // Common Response
        CommonResponse<MenuResponse> menuCommonResponse = CommonResponse.<MenuResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_DELETE_DATA)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(menuCommonResponse);
    }

}
