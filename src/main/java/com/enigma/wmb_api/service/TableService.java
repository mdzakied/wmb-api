package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.table.PostTableRequest;
import com.enigma.wmb_api.dto.request.table.PutTableRequest;
import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
import com.enigma.wmb_api.entity.MTable;
import org.springframework.data.domain.Page;

public interface TableService {
    MTable create(PostTableRequest postTableRequest);
    Page<MTable> getAll(SearchTableRequest searchTableRequest);
    MTable getById(String id);
    MTable update(PutTableRequest putTableRequest);
    void deleteById(String id);
}
