package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.table.PostTableRequest;
import com.enigma.wmb_api.dto.request.table.PutTableRequest;
import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
import com.enigma.wmb_api.dto.response.TableResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponsePage;
import com.enigma.wmb_api.service.TableService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class TableControllerTest {

    @MockBean
    private TableService tableService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave201StatusAndReturnCommonResponseWhenCreateTable() throws Exception {
        // Given params
        PostTableRequest request = PostTableRequest.builder()
                .name("T01")
                .build();

        // Given Response
        TableResponse tableResponse = TableResponse.builder()
                .id("t-01")
                .name(request.getName())
                .build();

        // Stabbing Config
        Mockito.when(tableService.create(Mockito.any(PostTableRequest.class)))
                .thenReturn(tableResponse);

        // Object Mapper
        String stringJson = objectMapper.writeValueAsString(request);

        // Then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(APIUrl.TABLE_API)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<TableResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(201, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_SAVE_DATA, response.getMessage());
                    assertEquals(response.getData().getName(), request.getName());
                });
    }


    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave200StatusAndReturnCommonResponseWhenGetAllTable() throws Exception {
        // Given Response
        TableResponse tableResponse = TableResponse.builder()
                .id("t-01")
                .name("T01")
                .build();

        // Given List Table
        List<TableResponse> tables = List.of(
                tableResponse
        );

        // Given Pageable from Tables
        Pageable pageable = PageRequest.of(1, 1);
        Page<TableResponse> tablePages = new PageImpl<>(tables, pageable, tables.size());

        // Stabbing Config
        Mockito.when(tableService.getAll(Mockito.any(SearchTableRequest.class)))
                .thenReturn(tablePages);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(APIUrl.TABLE_API))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponsePage<List<TableResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                    assertEquals(response.getData().size(), tablePages.getSize());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave200StatusAndReturnCommonResponseWhenGetTableById() throws Exception {
        // Given param
        String id = "t-01";

        // Given Response
        TableResponse tableResponse = TableResponse.builder()
                .id(id)
                .name("T01")
                .build();

        // Stabbing Config
        Mockito.when(tableService.getOneById(id))
                .thenReturn(tableResponse);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(APIUrl.TABLE_API + "/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<TableResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                    assertEquals(id, response.getData().getId());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave201StatusAndReturnCommonResponseWhenUpdateTable() throws Exception {
        // Given param
        String id = "t-01";

        // Given params
        PutTableRequest request = PutTableRequest.builder()
                .id(id)
                .name("T01")
                .build();

        // Given Response
        TableResponse tableResponse = TableResponse.builder()
                .id(request.getId())
                .name(request.getName())
                .build();

        // Stabbing Config
        Mockito.when(tableService.update(Mockito.any(PutTableRequest.class)))
                .thenReturn(tableResponse);

        // Object Mapper
        String stringJson = objectMapper.writeValueAsString(request);

        // Then
        mockMvc.perform(
                        MockMvcRequestBuilders.put(APIUrl.TABLE_API)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<TableResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(201, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_UPDATE_DATA, response.getMessage());
                    assertEquals(response.getData().getName(), request.getName());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave200StatusAndReturnCommonResponseWhenDeleteTableById() throws Exception {
        // Given param
        String id = "t-01";

        // Stubbing Config Delete
        Mockito.doNothing().when(tableService).deleteById(id);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete(APIUrl.TABLE_API + "/" + id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_DELETE_DATA, response.getMessage());
                });
    }
}