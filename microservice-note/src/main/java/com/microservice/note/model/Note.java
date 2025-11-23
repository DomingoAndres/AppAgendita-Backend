package com.microservice.note.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notes")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "El titulo de la nota es requerido")
    @Size(max = 200, message = "El titulo de la nota no puede exceder los 200 caracteres")
    private String title; 

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "La descripcion de la nota no puede exceder los 2000 caracteres")
    private String description;

    @Column(name = "image_uri")
    private String imageUri;

    @Column(name ="user_id", nullable = false)
    @NotNull(message = "El ID del usuario es requerido")
    private UUID userId;

    // Auditoria automatica
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
