package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.menu.PostMenuRequest;
import com.enigma.wmb_api.dto.request.menu.PutMenuRequest;
import com.enigma.wmb_api.dto.request.menu.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repositry.MenuRepository;
import com.enigma.wmb_api.service.ImageService;
import com.enigma.wmb_api.service.MenuService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {
    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private ImageService imageService;

    private MenuService menuService;


    @BeforeEach
    void setUp() {
        menuService = new MenuServiceImpl(menuRepository, validationUtil, imageService);
    }

    @Test
    void shouldReturnMenuResponseWhenCreate() {
        // Given Multipart File
        byte[] fileContent = "Content of the file".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("nasi-merah", "nasi-merah.jpg", "images/jpeg", fileContent);

        // Given Parameter Request
        PostMenuRequest parameterMenu = PostMenuRequest.builder()
                .name("Nasi Merah")
                .price(5000)
                .image(multipartFile)
                .build();

        // Given Image
        Image image = Image.builder()
                .id("img-01")
                .name(multipartFile.getName())
                .contentType(multipartFile.getContentType())
                .path("/api/menus/images")
                .size(50)
                .build();
        ;

        // Given Menu
        Menu menu = Menu.builder()
                .name(parameterMenu.getName())
                .price(parameterMenu.getPrice())
                .image(image)
                .build();

        // Stubbing Config save menu
        Mockito.when(menuRepository.saveAndFlush(Mockito.any(Menu.class)))
                .thenReturn(menu);

        // When
        MenuResponse actualMenu = menuService.create(parameterMenu);

        // Then
        assertEquals(parameterMenu.getName(), actualMenu.getName());
    }

    @Test
    void shouldReturnPageableMenuResponseWhenGetAll() {
        // Given Multipart File
        byte[] fileContent = "Content of the file".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("nasi-merah", "nasi-merah.jpg", "images/jpeg", fileContent);

        // Given Image
        Image image = Image.builder()
                .id("img-01")
                .name(multipartFile.getName())
                .contentType(multipartFile.getContentType())
                .path("/api/menus/images")
                .size(50)
                .build();
        ;

        // Given entity menu
        Menu menu = Menu.builder()
                .id("p-01")
                .name("Nasi Merah")
                .price(5000)
                .image(image)
                .build();

        // Given List Menu
        List<Menu> menus = List.of(
                menu
        );

        // Given Pageable from Menus
        Pageable pageable = PageRequest.of(1, 1);
        Page<Menu> menuPages = new PageImpl<>(menus, pageable, menus.size());

        // Stubbing Config find all menu
        Mockito.when(menuRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(menuPages);

        // When
        Page<MenuResponse> actualMenu = menuService.getAll(SearchMenuRequest.builder()
                .page(1)
                .size(10)
                .sortBy("name")
                .direction("asc")
                .build());

        // Then
        assertEquals(menuPages.getTotalElements(), actualMenu.getTotalElements());
    }

    @Test
    void shouldReturnMenuResponseWhenGetOneById() {
        // Given Param
        String id = "p-01";

        // Given Multipart File
        byte[] fileContent = "Content of the file".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("nasi-merah", "nasi-merah.jpg", "images/jpeg", fileContent);

        // Given Image
        Image image = Image.builder()
                .id("img-01")
                .name(multipartFile.getName())
                .contentType(multipartFile.getContentType())
                .path("/api/menus/images")
                .size(50)
                .build();
        ;

        // Given entity menu
        Menu menu = Menu.builder()
                .id("p-01")
                .name("Nasi Merah")
                .price(5000)
                .image(image)
                .build();

        // Stubbing Config find menu
        Mockito.when(menuRepository.findById(id))
                .thenReturn(Optional.of(menu));

        // When
        MenuResponse actualMenu = menuService.getOneById(id);

        // Then
        assertEquals(id, actualMenu.getId());
    }

    @Test
    void shouldReturnMenuWhenGetById() {
        // Given Param
        String id = "p-01";

        // Given Multipart File
        byte[] fileContent = "Content of the file".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("nasi-merah", "nasi-merah.jpg", "images/jpeg", fileContent);

        // Given Image
        Image image = Image.builder()
                .id("img-01")
                .name(multipartFile.getName())
                .contentType(multipartFile.getContentType())
                .path("/api/menus/images")
                .size(50)
                .build();
        ;

        // Given entity menu
        Menu menu = Menu.builder()
                .id("p-01")
                .name("Nasi Merah")
                .price(5000)
                .image(image)
                .build();

        // Stubbing Config find menu
        Mockito.when(menuRepository.findById(id))
                .thenReturn(Optional.of(menu));

        // When
        Menu actualMenu = menuService.getById(id);

        // Then
        assertEquals(id, actualMenu.getId());
    }

    @Test
    void shouldReturnMenuResponseWhenUpdate() {
        // Given Param
        String id = "p-01";

        // Given Multipart File
        byte[] fileContent = "Content of the file".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("nasi-merah", "nasi-merah.jpg", "images/jpeg", fileContent);

        // Given Parameter Request
        PutMenuRequest parameterMenu = PutMenuRequest.builder()
                .id(id)
                .name("Nasi Merah")
                .price(5000)
                .image(multipartFile)
                .build();

        // Given Image
        Image image = Image.builder()
                .id("img-01")
                .name(multipartFile.getName())
                .contentType(multipartFile.getContentType())
                .path("/api/menus/images")
                .size(50)
                .build();
        ;

        // Given entity menu
        Menu menu = Menu.builder()
                .id("p-01")
                .name("Nasi Kuning")
                .price(4000)
                .image(image)
                .build();

        // Stubbing Config find menu
        Mockito.when(menuRepository.findById(id))
                .thenReturn(Optional.of(menu));

        // Set Menu
        menu.setName(parameterMenu.getName());

        // Stubbing Config save menu
        Mockito.when(menuRepository.saveAndFlush(Mockito.any(Menu.class)))
                .thenReturn(menu);

        // When
        MenuResponse actualMenu = menuService.update(parameterMenu);

        // Then
        assertEquals(parameterMenu.getName(), actualMenu.getName());
    }

    @Test
    void shouldDeleteById() {
        // Given Params
        String id = "p-01";

        // Given Multipart File
        byte[] fileContent = "Content of the file".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("nasi-merah", "nasi-merah.jpg", "images/jpeg", fileContent);


        // Given Image
        Image image = Image.builder()
                .id("img-01")
                .name(multipartFile.getName())
                .contentType(multipartFile.getContentType())
                .path("/api/menus/images")
                .size(50)
                .build();
        ;

        // Given entity menu
        Menu menu = Menu.builder()
                .id("p-01")
                .name("Nasi Kuning")
                .price(4000)
                .image(image)
                .build();

        // Stubbing Config find menu
        Mockito.when(menuRepository.findById(id))
                .thenReturn(Optional.of(menu));

        // Stubbing Config Delete
        Mockito.doNothing().when(menuRepository).delete(menu);

        // When
        menuService.deleteById(id);

        // Then
        Mockito.verify(menuRepository, Mockito.times(1))
                .delete(menu);
    }

    @Test
    void shouldReturnMenuWhenFindByIdOrThrowNotFound() {
        // Given Param
        String id = "p-01";

        // Given Multipart File
        byte[] fileContent = "Content of the file".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("nasi-merah", "nasi-merah.jpg", "images/jpeg", fileContent);

        // Given Image
        Image image = Image.builder()
                .id("img-01")
                .name(multipartFile.getName())
                .contentType(multipartFile.getContentType())
                .path("/api/menus/images")
                .size(50)
                .build();
        ;

        // Given entity menu
        Menu menu = Menu.builder()
                .id("p-01")
                .name("Nasi Merah")
                .price(5000)
                .image(image)
                .build();

        // Stubbing Config find menu
        Mockito.when(menuRepository.findById(id))
                .thenReturn(Optional.of(menu));

        // When
        Menu actualMenu = menuService.getById(id);

        // Then
        assertEquals(id, actualMenu.getId());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenGetById(){
        assertThrows(RuntimeException.class, () -> {
            menuService.getById("Random Id");
        });
    }
}