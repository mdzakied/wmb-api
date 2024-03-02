package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.table.PostTableRequest;
import com.enigma.wmb_api.dto.request.table.PutTableRequest;
import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
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
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {
    private final TableRepository tableRepository;
    private final ValidationUtil validationUtil;

    @Override
    public MTable create(PostTableRequest postTableRequest) {
        // Validate postTableRequest
        validationUtil.validate(postTableRequest);

        MTable table = MTable.builder()
                .name(postTableRequest.getName())
                .build();

        return tableRepository.saveAndFlush(table);
    }

    @Override
    public Page<MTable> getAll(SearchTableRequest searchTableRequest) {
        // Validate Page
        if (searchTableRequest.getPage() <=0) searchTableRequest.setPage(1);
        // Sort
        Sort sort = Sort.by(Sort.Direction.fromString(searchTableRequest.getDirection()), searchTableRequest.getSortBy());
        // Pageable
        Pageable pageable = PageRequest.of(searchTableRequest.getPage() - 1, searchTableRequest.getSize(), sort);
        // Specification
        Specification<MTable> specification = TableSpecification.getSpecification(searchTableRequest);

        return tableRepository.findAll(specification, pageable);
    }

    @Override
    public MTable getById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    @Override
    public MTable update(PutTableRequest putTableRequest) {
        // Validate putMTableRequest
        validationUtil.validate(putTableRequest);

        // Get by Id
        MTable table = getById(putTableRequest.getId());

        MTable editedMTable = MTable.builder()
                .id(table.getId())
                .name(putTableRequest.getName())
                .build();

        return tableRepository.saveAndFlush(editedMTable);
    }

    @Override
    public void deleteById(String id) {
        // Get by Id
        MTable table = getById(id);

        tableRepository.delete(table);
    }

    public MTable findByIdOrThrowNotFound(String id){
        return tableRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "table not found"
                )
        );
    }
}
