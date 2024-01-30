package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void getUsers() {
        assertTrue(true);
    }

    @Test
    public void shouldThrowCheckIfOwnerExists() {
        long ownerId = 1L;
        Mockito.when(userRepository.existsById(ownerId)).thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> userService.checkIfOwnerExists(ownerId));
    }

    @Test
    void shouldThrowFindUserById() {
        long ownerId = 2L;
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> userService.findUserById(ownerId));
    }

    @Test
    void successFundUserById() {
        User expectedUser = User.builder()
                .id(1L)
                .active(true)
                .build();
        Mockito.when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.ofNullable(expectedUser));
        User actualUser = userService.findUserById(expectedUser.getId());
        assertEquals(expectedUser, actualUser);
    }
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