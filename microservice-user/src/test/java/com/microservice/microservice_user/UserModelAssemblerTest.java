package com.microservice.microservice_user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import com.microservice.microservice_user.assembler.UserModelAssembler;
import com.microservice.microservice_user.dto.UserDTO;

class UserModelAssemblerTest {

    private UserModelAssembler assembler;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        assembler = new UserModelAssembler();
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");
        userDTO.setEmail("test@duocuc.cl");
        userDTO.setRol("USER");
    }

    @Test
    void testToModel_addsLinks() {
        EntityModel<UserDTO> model = assembler.toModel(userDTO);

        assertNotNull(model.getContent());
        assertEquals(userDTO, model.getContent());

        // Verifica que el self link esté presente
        Link selfLink = model.getLink("self").orElse(null);
        assertNotNull(selfLink);
        assertTrue(selfLink.getHref().contains("/api/v2/users/1"));

        // Verifica que el link de users esté presente
        Link usersLink = model.getLink("users").orElse(null);
        assertNotNull(usersLink);
        assertTrue(usersLink.getHref().contains("/api/v2/users"));
    }
}