package com.microservice.microservice_user;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.microservice.microservice_user.client.clientSale;
import com.microservice.microservice_user.dto.SaleDTO;
import com.microservice.microservice_user.dto.UserDTO;
import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.repository.UserRepository;
import com.microservice.microservice_user.service.UserService;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private clientSale clientSale;

    @InjectMocks
    private UserService userService;

    private User user;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@duocuc.cl");
        user.setRol("USER");
    }

    //////// PARA EL METODO saveUser
    @Test
    void testSaveUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");
        userDTO.setEmail("test@duocuc.cl");
        userDTO.setRol("USER");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@duocuc.cl");
        savedUser.setRol("USER");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.saveUser(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getName(), result.getName());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getRol(), result.getRol());
    }



    //////// PARA EL METODO updateUser
    @Test
    void testUpdateUser() {
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setName("Updated User");
        updatedUserDTO.setEmail("updated@duocuc.cl");
        updatedUserDTO.setRol("ADMIN");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Test User");
        existingUser.setEmail("test@duocuc.cl");
        existingUser.setRol("USER");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@duocuc.cl");
        updatedUser.setRol("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO result = userService.updateUser(1L, updatedUserDTO);

        assertNotNull(result);
        assertEquals(updatedUserDTO.getName(), result.getName());
        assertEquals(updatedUserDTO.getEmail(), result.getEmail());
        assertEquals(updatedUserDTO.getRol(), result.getRol());
    }

    @Test
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRol(), result.getRol());
    }

    //////// PARA EL METODO findall
    @Test
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDTO> users = userService.findall();
        assertNotNull(users);
        assertEquals(1, users.size());
    }


    //////// PARA EL METODO deleteById 
    @Test
    void testDeleteById() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }


    //////// PARA EL METEDO authenticateById //////////
    @Test
    void testAuthenticateById_true() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean authenticated = userService.authenticateById(1L, "test@duocuc.cl", "USER");
        assertTrue(authenticated);
    }

    @Test
    void testAuthenticateById_false_wrongEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean authenticated = userService.authenticateById(1L, "otro@duocuc.cl", "USER");
        assertFalse(authenticated);
    }

    @Test
    void testAuthenticateById_false_wrongRol() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean authenticated = userService.authenticateById(1L, "test@duocuc.cl", "ADMIN");
        assertFalse(authenticated);
    }

    @Test
    void testAuthenticateById_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        boolean authenticated = userService.authenticateById(99L, "test@duocuc.cl", "USER");
        assertFalse(authenticated);
    }

    @Test
    void testAuthenticateById_emailNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean authenticated = userService.authenticateById(1L, null, "USER");
        assertFalse(authenticated);
    }

    @Test
    void testAuthenticateById_rolNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean authenticated = userService.authenticateById(1L, "test@duocuc.cl", null);
        assertFalse(authenticated);
    }

    @Test
    void testAuthenticateById_emailAndRolNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean authenticated = userService.authenticateById(1L, null, null);
        assertFalse(authenticated);
    }

    @Test
    void testAuthenticateById_userEmailNull() {
        user.setEmail(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean authenticated = userService.authenticateById(1L, "test@duocuc.cl", "USER");
        assertFalse(authenticated);
    }

    @Test
    void testAuthenticateById_userRolNull() {
        user.setRol(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean authenticated = userService.authenticateById(1L, "test@duocuc.cl", "USER");
        assertFalse(authenticated);
    }

    //////// PARA EL METODO findById
    @Test
    void testFindAllSaleByUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<SaleDTO> sales = List.of(new SaleDTO());
        when(clientSale.findAllSaleByUser(1L)).thenReturn(sales);

        List<SaleDTO> result = userService.findAllSaleByUser(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateUser_userNotFound() {
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setName("Updated User");
        updatedUserDTO.setEmail("updated@duocuc.cl");
        updatedUserDTO.setRol("ADMIN");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(99L, updatedUserDTO);
        });

        assertEquals("User not found with id: 99", exception.getMessage());
    }

    @Test
    void testFindById_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(99L);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAuthenticateById_invalidCredentials() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@duocuc.cl");
        user.setRol("USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = userService.authenticateById(1L, "wrong@duocuc.cl", "ADMIN");
        assertFalse(result);
    }

    @Test
    void testFindAllSaleByUser_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findAllSaleByUser(99L);
        });

        assertEquals("User not found.", exception.getMessage());
    }
}