package com.microservice.microservice_user.repository;
import com.microservice.microservice_user.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {
    
    Optional<UserPreferences> findByUserId(UUID userId);
    
    boolean existsByUserId(UUID userId);
}