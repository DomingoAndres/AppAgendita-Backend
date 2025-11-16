package com.microservice.microservice_user;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.microservice.microservice_user.controller.UserController;
import com.microservice.microservice_user.dto.SaleDTO;
import com.microservice.microservice_user.dto.UserDTO;
import com.microservice.microservice_user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListAllUsers_withUsers() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@duocuc.cl");

        when(userService.findall()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test User"));
    }

    @Test
    void testListAllUsers_noUsers() throws Exception {
        when(userService.findall()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSaveUser() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@duocuc.cl");

        when(userService.saveUser(any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testAuthenticateById_success() throws Exception {
        when(userService.authenticateById(1L, "test@duocuc.cl", "USER")).thenReturn(true);

        mockMvc.perform(post("/api/v1/users/authenticate-id")
                .param("id", "1")
                .param("email", "test@duocuc.cl")
                .param("rol", "USER"))
                .andExpect(status().isOk())
                .andExpect(content().string("ID authentication successful"));
    }

    @Test
    void testAuthenticateById_invalid() throws Exception {
        when(userService.authenticateById(1L, "wrong@duocuc.cl", "USER")).thenReturn(false);

        mockMvc.perform(post("/api/v1/users/authenticate-id")
                .param("id", "1")
                .param("email", "wrong@duocuc.cl")
                .param("rol", "USER"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testFindSalesByUser() throws Exception {
        SaleDTO sale = new SaleDTO();
        sale.setId(1L);
        sale.setClientID(1L);

        when(userService.findAllSaleByUser(1L)).thenReturn(List.of(sale));

        mockMvc.perform(get("/api/v1/users/search-sale-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testFindSalesByUser_notFound() throws Exception {
        when(userService.findAllSaleByUser(99L)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/users/search-sale-by-id/99"))
                .andExpect(status().isNotFound());
    }
}