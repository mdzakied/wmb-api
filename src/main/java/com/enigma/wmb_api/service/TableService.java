package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.table.PostTableRequest;
import com.enigma.wmb_api.dto.request.table.PutTableRequest;
import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
import com.enigma.wmb_api.dto.response.TableResponse;
import com.enigma.wmb_api.entity.MTable;
import org.springframework.data.domain.Page;

public interface TableService {
    TableResponse create(PostTableRequest postTableRequest);
    Page<TableResponse> getAll(SearchTableRequest searchTableRequest);
    TableResponse getOneById(String id);
    MTable getById(String id);
    TableResponse update(PutTableRequest putTableRequest);
    void deleteById(String id);
}
