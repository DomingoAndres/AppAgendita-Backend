package com.microservice.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "note_id", nullable = false)
    @NotNull(message = "Note ID is required")
    private UUID noteId;

    @Column(name = "owner_id", nullable = false)
    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_participants", joinColumns = @JoinColumn(name = "event_id"))
    private Set<Participant> participants = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Participant {

        @Column(name = "user_id", nullable = false)
        private UUID userId;

        @Column(name = "permission", nullable = false)
        @Enumerated(EnumType.STRING)
        private Permission permission;

        public enum Permission {
            READ,
            WRITE
        }
    }
}
