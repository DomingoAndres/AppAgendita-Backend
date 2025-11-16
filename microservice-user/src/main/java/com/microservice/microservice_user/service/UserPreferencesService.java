package com.microservice.microservice_user.service;

import com.microservice.microservice_user.assembler.UserAssembler;
import com.microservice.microservice_user.dto.UserPreferencesDTO;
import com.microservice.microservice_user.exception.UserNotFoundException;
import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.model.UserPreferences;
import com.microservice.microservice_user.repository.UserPreferencesRepository;
import com.microservice.microservice_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;
    private final UserAssembler userAssembler;

    @Autowired
    public UserPreferencesService(UserPreferencesRepository preferencesRepository,
                                  UserRepository userRepository,
                                  UserAssembler userAssembler) {
        this.preferencesRepository = preferencesRepository;
        this.userRepository = userRepository;
        this.userAssembler = userAssembler;
    }

    @Transactional(readOnly = true)
    public UserPreferencesDTO getUserPreferences(UUID userId) {
        UserPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
        
        return userAssembler.toPreferencesDTO(preferences);
    }

    public UserPreferencesDTO updateUserPreferences(UUID userId, UserPreferencesDTO preferencesDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));

        UserPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> new UserPreferences(user));

        preferences.setTheme(preferencesDTO.getTheme());
        preferences.setLanguage(preferencesDTO.getLanguage());
        preferences.setNotificationsEnabled(preferencesDTO.getNotificationsEnabled());
        preferences.setEmailNotifications(preferencesDTO.getEmailNotifications());
        preferences.setPushNotifications(preferencesDTO.getPushNotifications());

        UserPreferences savedPreferences = preferencesRepository.save(preferences);
        return userAssembler.toPreferencesDTO(savedPreferences);
    }

    private UserPreferences createDefaultPreferences(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));

        UserPreferences defaultPreferences = new UserPreferences(user);
        defaultPreferences.setTheme("LIGHT");
        defaultPreferences.setLanguage("ES");
        defaultPreferences.setNotificationsEnabled(true);
        defaultPreferences.setEmailNotifications(true);
        defaultPreferences.setPushNotifications(true);

        return preferencesRepository.save(defaultPreferences);
    }
}