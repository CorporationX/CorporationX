package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void getUsers() {
        assertTrue(true);
    }

    @Test
    public void whenOwnerExist_shouldReturnTrue() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        assertTrue(userService.checkIfOwnerExistsById(1L));
    }

    @Test
    public void whenOwnerDoesNotExist_shouldReturnFalse() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);
        assertFalse(userService.checkIfOwnerExistsById(1L));
    }

}