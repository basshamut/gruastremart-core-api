package com.gruastremart.api.unit.controller;

import com.gruastremart.api.controller.UserController;
import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    void testRegister() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        Mockito.when(userService.register(any(UserDto.class))).thenReturn(userDto);

        // Act
        UserDto result = userController.register(userDto).getBody();

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    // Test para el método 'update' del UserController
    @Test
    void testUpdate() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("updated@example.com");

        Mockito.when(userService.update(eq("1"), any(UserDto.class))).thenReturn(userDto);

        // Act
        UserDto result = userController.update("1", userDto).getBody(); // Llamada directa al método del controller

        // Assert
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void testFindWithFilters() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        Page<UserDto> page = new PageImpl<>(List.of(userDto));

        Mockito.when(userService.findWithFilters(any())).thenReturn(page);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "10");

        // Act
        Page<UserDto> result = userController.findWithFilters(params).getBody();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("test@example.com", result.getContent().getFirst().getEmail());
    }

}
