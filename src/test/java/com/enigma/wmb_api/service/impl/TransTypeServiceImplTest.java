package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.TransTypeEnum;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.TransType;
import com.enigma.wmb_api.repositry.TransTypeRepository;
import com.enigma.wmb_api.service.TransTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TransTypeServiceImplTest {

    @Mock
    private TransTypeRepository transTypeRepository;

    TransTypeService transTypeService;

    @BeforeEach
    void setUp() {
        transTypeService = new TransTypeServiceImpl(transTypeRepository);
    }

    @Test
    void shouldReturnTransTypeWhenGetById() {
        // Given param
        String id = TransTypeEnum.TA.name();

        // Given TransType
        TransType transType = TransType.builder()
                .transTypeEnum(TransTypeEnum.TA)
                .description("Take Away")
                .build();

        // Stubbing Config find transType
        Mockito.when(transTypeRepository.findByTransTypeEnum(Mockito.any()))
                .thenReturn(transType);

        // When
        TransType actualTransType = transTypeService.getById(id);

        // Then
        assertEquals(id, actualTransType.getTransTypeEnum().name());
    }
}