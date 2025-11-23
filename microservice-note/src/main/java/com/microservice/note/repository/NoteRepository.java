package com.microservice.note.repository;
import com.microservice.note.model.Note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    // 1. BÁSICO: Obtener todas las notas de un usuario (Ordenadas por fecha de creación descendente)
    List<Note> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // 2. PAGINACIÓN: Igual que el anterior, pero devuelve una "página" de resultados
    // Útil para "scrolling infinito" en Android
    Page<Note> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // 3. BÚSQUEDA: Buscar texto en título O descripción (Case Insensitive)
    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(n.description) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Note> searchNotes(@Param("userId") UUID userId, @Param("searchText") String searchText);

    // 4. ESTADÍSTICAS: Contar cuántas notas tiene el usuario (para el perfil)
    long countByUserId(UUID userId);

    // 5. SEGURIDAD: Verificar si una nota pertenece a un usuario antes de borrar/editar
    boolean existsByIdAndUserId(UUID id, UUID userId);
    
    // 6. LIMPIEZA: Borrar todo de un usuario (útil si el usuario elimina su cuenta)
    void deleteByUserId(UUID userId);
}