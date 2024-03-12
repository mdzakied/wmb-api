package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.table.PostTableRequest;
import com.enigma.wmb_api.dto.request.table.PutTableRequest;
import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
import com.enigma.wmb_api.dto.response.TableResponse;
import com.enigma.wmb_api.entity.MTable;
import com.enigma.wmb_api.repositry.TableRepository;
import com.enigma.wmb_api.service.TableService;
import com.enigma.wmb_api.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TableServiceImplTest {

    @Mock
    private TableRepository tableRepository;
    @Mock
    private ValidationUtil validationUtil;

    private TableService tableService;

    @BeforeEach
    void setUp() {
        tableService = new TableServiceImpl(tableRepository, validationUtil);
    }

    @Test
    void shouldReturnTableWhenCreate() {
        // Given Parameter Request
        PostTableRequest parameterTable = PostTableRequest.builder()
                .name("T01")
                .build();

        // Given Table
        MTable table = MTable.builder()
                .name(parameterTable.getName())
                .build();

        // Stubbing Config save table
        Mockito.when(tableRepository.saveAndFlush(Mockito.any(MTable.class)))
                .thenReturn(table);

        // When
        TableResponse actualTable = tableService.create(parameterTable);

        // Then
        assertEquals(parameterTable.getName(), actualTable.getName());
    }

    @Test
    void shouldReturnPageableTableWhenGetAll() {
        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("Ty01")
                .build();

        // Given List Menu
        List<MTable> tables = List.of(
                table
        );

        // Given Pageable from Tables
        Pageable pageable = PageRequest.of(1, 1);
        Page<MTable> tablePages = new PageImpl<>(tables, pageable, tables.size());

        // Stubbing Config find all table
        Mockito.when(tableRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(tablePages);

        // When
        Page<TableResponse> actualMenu = tableService.getAll(SearchTableRequest.builder()
                .page(1)
                .size(10)
                .sortBy("name")
                .direction("asc")
                .build());

        // Then
        assertEquals(tablePages.getTotalElements(), actualMenu.getTotalElements());
    }

    @Test
    void shouldReturnTableResponseWhenGetOneById() {
        // Given Param
        String id = "t-01";

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("Ty01")
                .build();

        // Stubbing Config find table
        Mockito.when(tableRepository.findById(id))
                .thenReturn(Optional.of(table));

        // When
        TableResponse actualTable = tableService.getOneById(id);

        // Then
        assertEquals(id, actualTable.getId());
    }

    @Test
    void shouldReturnTableWhenGetById() {
        // Given Param
        String id = "t-01";

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("Ty01")
                .build();

        // Stubbing Config find table
        Mockito.when(tableRepository.findById(id))
                .thenReturn(Optional.of(table));

        // When
        MTable actualTable = tableService.getById(id);

        // Then
        assertEquals(id, actualTable.getId());
    }

    @Test
    void shouldReturnTableResponseWhenUpdate() {
        // Given Param
        String id = "t-01";

        // Given Parameter Request
        PutTableRequest parameterTable = PutTableRequest.builder()
                .id(id)
                .name("T01")
                .build();

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("Ty01")
                .build();

        // Stubbing Config find table
        Mockito.when(tableRepository.findById(id))
                .thenReturn(Optional.of(table));

        // Set Table
        table.setName(parameterTable.getName());

        // Stubbing Config save table
        Mockito.when(tableRepository.saveAndFlush(Mockito.any(MTable.class)))
                .thenReturn(table);

        // When
        TableResponse actualTable = tableService.update(parameterTable);

        // Then
        assertEquals(parameterTable.getName(), actualTable.getName());
    }

    @Test
    void shouldDeleteById() {
        // Given Param
        String id = "t-01";

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("Ty01")
                .build();

        // Stubbing Config find table
        Mockito.when(tableRepository.findById(id))
                .thenReturn(Optional.of(table));

        // Stubbing Config Delete
        Mockito.doNothing().when(tableRepository).delete(table);

        // When
        tableService.deleteById(id);

        // Then
        Mockito.verify(tableRepository, Mockito.times(1))
                .delete(table);
    }

    @Test
    void shouldReturnTableWhenFindByIdOrThrowNotFound() {
        // Given Param
        String id = "t-01";

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("Ty01")
                .build();

        // Stubbing Config find table
        Mockito.when(tableRepository.findById(id))
                .thenReturn(Optional.of(table));

        // When
        MTable actualTable = tableService.getById(id);

        // Then
        assertEquals(id, actualTable.getId());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenGetById() {
        assertThrows(RuntimeException.class, () -> {
            tableService.getById("Random Id");
        });
    }
}