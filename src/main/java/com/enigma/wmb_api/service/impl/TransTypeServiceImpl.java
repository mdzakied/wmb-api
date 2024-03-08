package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.TransTypeEnum;
import com.enigma.wmb_api.entity.TransType;
import com.enigma.wmb_api.repositry.TransTypeRepository;
import com.enigma.wmb_api.service.TransTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TransTypeServiceImpl implements TransTypeService {
    private final TransTypeRepository transTypeRepository;

    @Override
    public TransType getById(String id) {
        return transTypeRepository.findByTransTypeEnum(TransTypeEnum.valueOf(id));
    }

}
