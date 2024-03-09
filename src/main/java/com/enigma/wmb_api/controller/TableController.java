package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.table.PutTableRequest;
import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
import com.enigma.wmb_api.dto.request.table.PostTableRequest;
import com.enigma.wmb_api.dto.response.TableResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponsePage;
import com.enigma.wmb_api.dto.response.common.PagingResponse;
import com.enigma.wmb_api.entity.MTable;
import com.enigma.wmb_api.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.TABLE_API)
public class TableController {
    private final TableService tableService;

    // Create Table Service
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<TableResponse>> createTable(
            @RequestBody PostTableRequest postTableRequest
    ) {
        // Table Response from crate Service
        TableResponse tableResponse = tableService.create(postTableRequest);

        // Common Response
        CommonResponse<TableResponse> tableCommonResponse = CommonResponse.<TableResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(ResponseMessage.SUCCESS_SAVE_DATA)
                .data(tableResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tableCommonResponse);

    }

    // Get All Table Controller
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponsePage<List<TableResponse>>> getAllTable (
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String soryBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction
    ) {
        // Query Params & Pagination to SearchTableRequest
        SearchTableRequest searchTableRequest = SearchTableRequest.builder()
                .name(name)
                .page(page)
                .size(size)
                .sortBy(soryBy)
                .direction(direction)
                .build();

        // Get All Table Response from getAll Service
        Page<TableResponse> tables = tableService.getAll(searchTableRequest);

        // Paging Response for Common Response
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(tables.getTotalPages())
                .totalElement(tables.getTotalElements())
                .page(tables.getPageable().getPageNumber() + 1)
                .size(tables.getPageable().getPageSize())
                .hasNext(tables.hasNext())
                .hasPrevious(tables.hasPrevious())
                .build();

        // Common Response
        CommonResponsePage<List<TableResponse>> response = CommonResponsePage.<List<TableResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(tables.getContent())
                .paging(pagingResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Get Table by Id Controller
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<TableResponse>> getTableById(
            @PathVariable String id
    ) {
        // Table Response from getOneById service
        TableResponse table = tableService.getOneById(id);

        // Common Response
        CommonResponse<TableResponse> response = CommonResponse.<TableResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(table)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Update Table Controller
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<TableResponse>> updateTable(
            @RequestBody PutTableRequest putTableRequest
    ) {
        // Table Response from update Service
        TableResponse table = tableService.update(putTableRequest);

        // Common Response
        CommonResponse<TableResponse> tableCommonResponse = CommonResponse.<TableResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_UPDATE_DATA)
                .data(table)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tableCommonResponse);
    }

    // Table Controller
    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<TableResponse>> deleteTableResponseById(
            @PathVariable String id
    ) {

        // Table Response from delete Service
        tableService.deleteById(id);

        // Common Response
        CommonResponse<TableResponse> tableCommonResponse = CommonResponse.<TableResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_DELETE_DATA)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tableCommonResponse);
    }

}
