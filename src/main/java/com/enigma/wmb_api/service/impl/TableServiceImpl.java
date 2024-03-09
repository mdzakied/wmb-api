package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.table.PostTableRequest;
import com.enigma.wmb_api.dto.request.table.PutTableRequest;
import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
import com.enigma.wmb_api.dto.response.TableResponse;
import com.enigma.wmb_api.entity.MTable;
import com.enigma.wmb_api.repositry.TableRepository;
import com.enigma.wmb_api.service.TableService;
import com.enigma.wmb_api.specification.TableSpecification;
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
public class TableServiceImpl implements TableService {
    private final TableRepository tableRepository;
    private final ValidationUtil validationUtil;

    // Create Table Service
    @Override
    public TableResponse create(PostTableRequest postTableRequest) {
        // Validate postTableRequest
        validationUtil.validate(postTableRequest);

        // Create Table
        MTable table = MTable.builder()
                .name(postTableRequest.getName())
                .build();
        
        // Save to Repository
        tableRepository.saveAndFlush(table);

        // Convert to Table Response
        return convertToTableResponse(table);
    }

    // Get All Table Service
    @Override
    public Page<TableResponse> getAll(SearchTableRequest searchTableRequest) {
        // Validate Page
        if (searchTableRequest.getPage() <=0) searchTableRequest.setPage(1);
        // Sort
        Sort sort = Sort.by(Sort.Direction.fromString(searchTableRequest.getDirection()), searchTableRequest.getSortBy());
        // Pageable
        Pageable pageable = PageRequest.of(searchTableRequest.getPage() - 1, searchTableRequest.getSize(), sort);
        // Specification
        Specification<MTable> specification = TableSpecification.getSpecification(searchTableRequest);

        // Find All Table with Pageable
        Page<MTable> tablePages = tableRepository.findAll(specification, pageable);
        
        // Response Page
        // Convert to Table Response
        return tablePages.map(this::convertToTableResponse);
    }

    // Get Table By Id Service (return tableResponse)
    @Transactional(readOnly = true)
    @Override
    public TableResponse getOneById(String id) {
        // Find by Id
        MTable table = findByIdOrThrowNotFound(id);

        // Convert to Table Response
        return convertToTableResponse(table);
    }

    // Get Table By Id Service (return table)
    @Transactional(readOnly = true)
    @Override
    public MTable getById(String id) {
        // Find By id (return entity)
        return findByIdOrThrowNotFound(id);
    }

    // Update Table Service
    @Transactional(rollbackFor = Exception.class)
    @Override
    public TableResponse update(PutTableRequest putTableRequest) {
        // Validate putMTableRequest
        validationUtil.validate(putTableRequest);

        // Get by Id
        MTable currentTable = getById(putTableRequest.getId());

        // Create Table
        MTable table = MTable.builder()
                .id(currentTable.getId())
                .name(putTableRequest.getName())
                .build();

        // Save to Repository
        tableRepository.saveAndFlush(table);

        // Convert to Table Response
        return convertToTableResponse(table);
    }

    // Delete Table Service
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        // Get by Id
        MTable table = getById(id);

        // Delete to Repository
        tableRepository.delete(table);
    }
    
    // Find Table or Throw Error Service
    @Transactional(readOnly = true)
    public MTable findByIdOrThrowNotFound(String id){
        return tableRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ResponseMessage.ERROR_NOT_FOUND
                )
        );
    }

    // Convert to Response Table Service
    public TableResponse convertToTableResponse(MTable table) {
        return TableResponse.builder()
                .id(table.getId())
                .name(table.getName())
                .build();
    }
}
