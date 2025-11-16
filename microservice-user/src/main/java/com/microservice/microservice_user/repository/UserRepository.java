package com.microservice.microservice_user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.model.UserRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByActiveTrue();

    List<User> findByActiveTrueAndRole(UserRole role);

    List<User> findByActiveTrueAndEmailVerifiedTrue();

    @Query("SELECT u FROM User u WHERE u.active = true AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, ''))) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findActiveUsersBySearch(@Param("search") String search, Pageable pageable);

    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    Long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.emailVerified = true")
    Long countVerifiedUsers();
}