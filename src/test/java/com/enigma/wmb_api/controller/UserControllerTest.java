package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.user.PutUserRequest;
import com.enigma.wmb_api.dto.request.user.SearchUserRequest;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponsePage;
import com.enigma.wmb_api.dto.response.user.RoleResponse;
import com.enigma.wmb_api.dto.response.user.UserAccountResponse;
import com.enigma.wmb_api.dto.response.user.UserResponse;
import com.enigma.wmb_api.service.UserService;
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
class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave200StatusAndReturnCommonResponseWhenGetAllUser() throws Exception {
        // Given Response
        UserResponse userResponse = UserResponse.builder()
                .id("u-01")
                .name("Dzaki")
                .phoneNumber("0881")
                .userAccount(UserAccountResponse.builder()
                        .id("ua-01")
                        .username("Dzaki")
                        .roles(List.of(
                                RoleResponse.builder()
                                        .id("r-01")
                                        .role("EI")
                                        .build()
                        ))
                        .build())
                .build();

        // Given List User
        List<UserResponse> users = List.of(
                userResponse
        );

        // Given Pageable from Users
        Pageable pageable = PageRequest.of(1, 1);
        Page<UserResponse> userPages = new PageImpl<>(users, pageable, users.size());

        // Stabbing Config
        Mockito.when(userService.getAll(Mockito.any(SearchUserRequest.class)))
                .thenReturn(userPages);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(APIUrl.USER_API))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponsePage<List<UserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                    assertEquals(response.getData().size(), userPages.getSize());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave200StatusAndReturnCommonResponseWhenGetUserById() throws Exception {
        // Given param
        String id = "u-01";

        // Given Response
        UserResponse userResponse = UserResponse.builder()
                .id(id)
                .name("Dzaki")
                .phoneNumber("0881")
                .userAccount(UserAccountResponse.builder()
                        .id("ua-01")
                        .username("Dzaki")
                        .roles(List.of(
                                RoleResponse.builder()
                                        .id("r-01")
                                        .role("EI")
                                        .build()
                        ))
                        .build())
                .build();

        // Stabbing Config
        Mockito.when(userService.getOneById(id))
                .thenReturn(userResponse);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(APIUrl.USER_API + "/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                    assertEquals(id, response.getData().getId());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave201StatusAndReturnCommonResponseWhenUpdateUser() throws Exception {
        // Given param
        String id = "u-01";

        // Given Params
        PutUserRequest request = PutUserRequest.builder()
                .id(id)
                .name("Dzaki")
                .phoneNumber("0881")
                .build();

        // Given Response
        UserResponse userResponse = UserResponse.builder()
                .id(id)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .userAccount(UserAccountResponse.builder()
                        .id("ua-01")
                        .username("Dzaki")
                        .roles(List.of(
                                RoleResponse.builder()
                                        .id("r-01")
                                        .role("EI")
                                        .build()
                        ))
                        .build())
                .build();

        // Stabbing Config
        Mockito.when(userService.update(Mockito.any(PutUserRequest.class)))
                .thenReturn(userResponse);

        // Object Mapper
        String stringJson = objectMapper.writeValueAsString(request);

        // Then
        mockMvc.perform(
                        MockMvcRequestBuilders.put(APIUrl.USER_API)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(201, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_UPDATE_DATA, response.getMessage());
                    assertEquals(response.getData().getName(), request.getName());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN", "ADMIN", "CUSTOMER"})
    @Test
    void shouldHave200StatusAndReturnCommonResponseWhenDeleteUserById() throws Exception {
        // Given param
        String id = "u-01";

        // Stubbing Config Delete
        Mockito.doNothing().when(userService).deleteById(id);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete(APIUrl.USER_API + "/" + id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_DELETE_DATA, response.getMessage());
                });
    }
}