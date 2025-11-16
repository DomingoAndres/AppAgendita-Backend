package com.microservice.microservice_user;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.repository.UserRepository;

@ActiveProfiles("test")
class DataLoaderTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DataLoader dataLoader;

    @Test
    void testRunInsertsUsers() throws Exception {
        MockitoAnnotations.openMocks(this);

        dataLoader.run();

        // esto verifica que el método save de userRepository se haya llamado 10 veces
        // ya que en el DataLoader se crean 10 usuarios
        verify(userRepository, times(10)).save(any(User.class));
    }
}