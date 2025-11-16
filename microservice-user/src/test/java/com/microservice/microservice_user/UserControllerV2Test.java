package com.microservice.microservice_user;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.microservice.microservice_user.assembler.UserModelAssembler;
import com.microservice.microservice_user.controller.UserControllerV2;
import com.microservice.microservice_user.dto.UserDTO;
import com.microservice.microservice_user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserControllerV2.class)
class UserControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserModelAssembler assembler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListAllUsers_withUsers() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("Test User");

        when(userService.findall()).thenReturn(List.of(user));
        when(assembler.toModel(any(UserDTO.class))).thenReturn(EntityModel.of(user));

        mockMvc.perform(get("/api/v2/users"))
                .andExpect(status().isOk())
                // Ajusta el nombre del array según tu CollectionModel, puede ser userDTOList, entityModelList, etc.
                .andExpect(jsonPath("$._embedded.userDTOList[0].id").value(1L));
    }

    @Test
    void testListAllUsers_noUsers() throws Exception {
        when(userService.findall()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v2/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFindById_found() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("Test User");

        when(userService.findById(1L)).thenReturn(user);
        when(assembler.toModel(any(UserDTO.class))).thenReturn(EntityModel.of(user));

        mockMvc.perform(get("/api/v2/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testFindById_notFound() throws Exception {
        when(userService.findById(99L)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v2/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveUser() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("Test User");

        when(userService.saveUser(any(UserDTO.class))).thenReturn(user);
        when(assembler.toModel(any(UserDTO.class))).thenReturn(EntityModel.of(user));

        mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDeleteUser_success() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/v2/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_notFound() throws Exception {
        doThrow(new RuntimeException()).when(userService).deleteById(99L);

        mockMvc.perform(delete("/api/v2/users/99"))
                .andExpect(status().isNotFound());
    }
}