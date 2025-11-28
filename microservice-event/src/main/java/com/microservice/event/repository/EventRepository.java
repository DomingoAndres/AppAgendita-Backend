package com.microservice.event.repository;

import com.microservice.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    // 1. BASIC: Get all events of a user (ordered by creation date desc)
    List<Event> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId);

    // 2. PAGINATION: Same as above, but paginated
    Page<Event> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId, Pageable pageable);

    // 3. SEARCH: Search text in title or description for that owner
    @Query("SELECT e FROM Event e WHERE e.ownerId = :ownerId AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Event> searchEvents(@Param("ownerId") UUID ownerId,
                             @Param("searchText") String searchText);

    // 4. STATS: Count how many events the user has
    long countByOwnerId(UUID ownerId);

    // 5. SECURITY: Check if an event belongs to the user before edit/delete
    boolean existsByIdAndOwnerId(UUID id, UUID ownerId);

    // 6. CLEANUP: Delete all events of a user (for when the user deletes the account)
    void deleteByOwnerId(UUID ownerId);
}
