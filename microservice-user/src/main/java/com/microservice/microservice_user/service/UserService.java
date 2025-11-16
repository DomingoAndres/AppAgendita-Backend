package com.microservice.microservice_user.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.microservice_user.assembler.UserAssembler;
import com.microservice.microservice_user.dto.*;
import com.microservice.microservice_user.exception.*;
import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.model.UserRole;
import com.microservice.microservice_user.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserAssembler userAssembler;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, 
                       UserAssembler userAssembler,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.userAssembler = userAssembler;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByActiveTrue();
        return userAssembler.toDTOList(users);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userAssembler::toDTO);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
        return userAssembler.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con username: " + username));
        return userAssembler.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + email));
        return userAssembler.toDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String search, Pageable pageable) {
        Page<User> users = userRepository.findActiveUsersBySearch(search, pageable);
        return users.map(userAssembler::toDTO);
    }

    public UserDTO createUser(CreateUserDTO createUserDTO) {
        // Validar que el username no exista
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario '" + createUserDTO.getUsername() + "' ya existe");
        }

        // Validar que el email no exista
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new EmailAlreadyExistsException("El email '" + createUserDTO.getEmail() + "' ya está registrado");
        }

        User user = userAssembler.fromCreateDTO(createUserDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return userAssembler.toDTO(savedUser);
    }

    public UserDTO updateUser(UUID id, UpdateUserDTO updateUserDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        User updatedUser = userAssembler.updateFromDTO(existingUser, updateUserDTO);
        User savedUser = userRepository.save(updatedUser);
        
        return userAssembler.toDTO(savedUser);
    }

    public boolean changePassword(UUID id, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);

        return true;
    }

    public boolean deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        user.setActive(false);
        userRepository.save(user);
        return true;
    }

    public boolean activateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    public boolean verifyEmail(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        user.setEmailVerified(true);
        userRepository.save(user);
        return true;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUsernameOrEmail(
                loginRequestDTO.getUsernameOrEmail(), 
                loginRequestDTO.getUsernameOrEmail()
        ).orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!user.getActive()) {
            throw new InvalidCredentialsException("La cuenta está desactivada");
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);
        
        return new LoginResponseDTO(token, userAssembler.toDTO(user));
    }

    // Métodos para estadísticas
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countActiveUsers());
        stats.put("verifiedUsers", userRepository.countVerifiedUsers());
        stats.put("adminUsers", userRepository.findByActiveTrueAndRole(UserRole.ADMIN).size());
        stats.put("regularUsers", userRepository.findByActiveTrueAndRole(UserRole.USER).size());
        return stats;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        List<User> users = userRepository.findByCreatedAtBetween(startDate, endDate);
        return userAssembler.toDTOList(users);
    }
}
