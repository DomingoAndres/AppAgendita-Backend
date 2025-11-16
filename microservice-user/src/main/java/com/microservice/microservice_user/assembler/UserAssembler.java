package com.microservice.microservice_user.assembler;

import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.model.UserPreferences;
import com.microservice.microservice_user.dto.CreateUserDTO;
import com.microservice.microservice_user.dto.UpdateUserDTO;
import com.microservice.microservice_user.dto.UserDTO;
import com.microservice.microservice_user.dto.UserPreferencesDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAssembler {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setRole(user.getRole());
        dto.setActive(user.getActive());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    public List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public User fromCreateDTO(CreateUserDTO createUserDTO) {
        if (createUserDTO == null) {
            return null;
        }

        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setEmail(createUserDTO.getEmail());
        user.setPassword(createUserDTO.getPassword()); // Será encriptada en el servicio
        user.setFirstName(createUserDTO.getFirstName());
        user.setLastName(createUserDTO.getLastName());
        user.setPhoneNumber(createUserDTO.getPhoneNumber());

        return user;
    }

    public User updateFromDTO(User user, UpdateUserDTO updateUserDTO) {
        if (user == null || updateUserDTO == null) {
            return user;
        }

        if (updateUserDTO.getFirstName() != null) {
            user.setFirstName(updateUserDTO.getFirstName());
        }
        if (updateUserDTO.getLastName() != null) {
            user.setLastName(updateUserDTO.getLastName());
        }
        if (updateUserDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        if (updateUserDTO.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateUserDTO.getProfileImageUrl());
        }

        return user;
    }

    public UserPreferencesDTO toPreferencesDTO(UserPreferences preferences) {
        if (preferences == null) {
            return null;
        }

        UserPreferencesDTO dto = new UserPreferencesDTO();
        dto.setId(preferences.getId());
        dto.setUserId(preferences.getUser().getId());
        dto.setTheme(preferences.getTheme());
        dto.setLanguage(preferences.getLanguage());
        dto.setNotificationsEnabled(preferences.getNotificationsEnabled());
        dto.setEmailNotifications(preferences.getEmailNotifications());
        dto.setPushNotifications(preferences.getPushNotifications());

        return dto;
    }

    public UserPreferences fromPreferencesDTO(UserPreferencesDTO preferencesDTO, User user) {
        if (preferencesDTO == null || user == null) {
            return null;
        }

        UserPreferences preferences = new UserPreferences();
        preferences.setId(preferencesDTO.getId());
        preferences.setUser(user);
        preferences.setTheme(preferencesDTO.getTheme());
        preferences.setLanguage(preferencesDTO.getLanguage());
        preferences.setNotificationsEnabled(preferencesDTO.getNotificationsEnabled());
        preferences.setEmailNotifications(preferencesDTO.getEmailNotifications());
        preferences.setPushNotifications(preferencesDTO.getPushNotifications());

        return preferences;
    }
}