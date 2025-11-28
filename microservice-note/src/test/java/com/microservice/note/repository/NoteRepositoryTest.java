package com.microservice.note.repository;

import com.microservice.note.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    private UUID userId;
    private Note note1;
    private Note note2;
    private Note noteOtherUser;

    @BeforeEach
    void setUp() {
        // Limpiamos la BD antes de cada test
        noteRepository.deleteAll();
        
        userId = UUID.randomUUID();

        // Nota 1: Antigua
        note1 = Note.builder()
                .title("Reunión de trabajo")
                .description("Discutir el proyecto Agendita")
                .userId(userId)
                .createdAt(LocalDateTime.now().minusDays(1)) // Ayer
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        // Nota 2: Reciente
        note2 = Note.builder()
                .title("Lista de compras")
                .description("Comprar leche y pan")
                .userId(userId)
                .createdAt(LocalDateTime.now()) // Hoy (Más reciente)
                .updatedAt(LocalDateTime.now())
                .build();

        // Nota 3: De otro usuario (para probar aislamiento)
        noteOtherUser = Note.builder()
                .title("Nota privada")
                .description("No deberías ver esto")
                .userId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Guardamos en la base de datos H2
        note1 = noteRepository.save(note1);
        note2 = noteRepository.save(note2);
        noteOtherUser = noteRepository.save(noteOtherUser);
    }

    // --- Test 1: Ordenamiento ---
    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnOrderedNotes() {
        List<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        assertThat(notes).hasSize(2);
        // La nota 2 es más reciente, debe ir primero
        assertThat(notes.get(0).getTitle()).isEqualTo("Lista de compras");
        assertThat(notes.get(1).getTitle()).isEqualTo("Reunión de trabajo");
    }

    // --- Test 2: Paginación ---
    @Test
    void findByUserIdOrderByCreatedAtDesc_Pageable_ShouldReturnPage() {
        Page<Note> page = noteRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 10));
        
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Lista de compras");
    }

    // --- Test 3: Búsqueda (El más importante para @Query) ---
    @Test
    void searchNotes_ShouldReturnMatchesInTitleOrDescription() {
        // Búsqueda por parte del título ("compras")
        List<Note> resultsTitle = noteRepository.searchNotes(userId, "compras");
        assertThat(resultsTitle).hasSize(1);
        assertThat(resultsTitle.get(0).getTitle()).isEqualTo("Lista de compras");

        // Búsqueda por parte de la descripción ("Agendita")
        List<Note> resultsDesc = noteRepository.searchNotes(userId, "Agendita");
        assertThat(resultsDesc).hasSize(1);
        assertThat(resultsDesc.get(0).getTitle()).isEqualTo("Reunión de trabajo");

        // Búsqueda Case Insensitive ("REUNIÓN")
        List<Note> resultsCase = noteRepository.searchNotes(userId, "REUNIÓN");
        assertThat(resultsCase).hasSize(1);

        // Búsqueda que no coincide con nada
        List<Note> resultsNone = noteRepository.searchNotes(userId, "Inexistente");
        assertThat(resultsNone).isEmpty();
        
        // Asegurar que no busca notas de otros usuarios
        List<Note> resultsOther = noteRepository.searchNotes(userId, "privada");
        assertThat(resultsOther).isEmpty();
    }

    // --- Test 4: Conteo ---
    @Test
    void countByUserId_ShouldReturnCorrectCount() {
        long count = noteRepository.countByUserId(userId);
        assertThat(count).isEqualTo(2);
    }

    // --- Test 5: Existencia y Propiedad ---
    @Test
    void existsByIdAndUserId_ShouldReturnTrueOnlyIfOwner() {
        // Caso positivo
        boolean exists = noteRepository.existsByIdAndUserId(note1.getId(), userId);
        assertThat(exists).isTrue();

        // Caso negativo (ID correcto, Usuario incorrecto)
        boolean notExists = noteRepository.existsByIdAndUserId(note1.getId(), UUID.randomUUID());
        assertThat(notExists).isFalse();
    }

    // --- Test 6: Borrado masivo ---
    @Test
    void deleteByUserId_ShouldDeleteOnlyUserNotes() {
        noteRepository.deleteByUserId(userId);

        // Verificar que las notas del usuario se fueron
        List<Note> userNotes = noteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        assertThat(userNotes).isEmpty();

        // Verificar que la nota del otro usuario sigue ahí
        assertThat(noteRepository.findById(noteOtherUser.getId())).isPresent();
    }
}