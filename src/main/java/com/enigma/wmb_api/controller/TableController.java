package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.table.PutTableRequest;
import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
import com.enigma.wmb_api.dto.request.table.PostTableRequest;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.PagingResponse;
import com.enigma.wmb_api.entity.MTable;
import com.enigma.wmb_api.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.TABLE_API)
public class TableController {
    private final TableService tableService;

    // Create Table
    @PostMapping
    public ResponseEntity<CommonResponse<MTable>> createTable(
            @RequestBody PostTableRequest postTableRequest
    ) {
        // Create Table to Service
        MTable table = tableService.create(postTableRequest);

        // Common Response
        CommonResponse<MTable> tableCommonResponse = CommonResponse.<MTable>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully create table")
                .data(table)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tableCommonResponse);

    }

    // Get All Table
    @GetMapping
    public ResponseEntity<CommonResponse<List<MTable>>> getAllTable (
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

        // Get All Table to Service
        Page<MTable> tables = tableService.getAll(searchTableRequest);

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
        CommonResponse<List<MTable>> response = CommonResponse.<List<MTable>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success get all table")
                .data(tables.getContent())
                .paging(pagingResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Get Table by Id
    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<MTable>> getTableById(
            @PathVariable String id
    ) {
        // Get MTable by Id to Service
        MTable table = tableService.getById(id);

        // Common Response
        CommonResponse<MTable> response = CommonResponse.<MTable>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully get table by id")
                .data(table)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Update Menu
    @PutMapping
    public ResponseEntity<CommonResponse<MTable>> updateTable(
            @RequestBody PutTableRequest putTableRequest
    ) {
        // Update MTable to Service
        MTable table = tableService.update(putTableRequest);

        // Common Response
        CommonResponse<MTable> tableCommonResponse = CommonResponse.<MTable>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully edit table")
                .data(table)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tableCommonResponse);
    }

    // Delete MTable by Id
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<MTable>> deleteMTableById(
            @PathVariable String id
    ) {

        // Delete MTable to Service
        tableService.deleteById(id);

        // Common Response
        CommonResponse<MTable> tableCommonResponse = CommonResponse.<MTable>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully delete table")
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tableCommonResponse);
    }

}
