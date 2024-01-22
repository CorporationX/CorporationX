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
import static org.mockito.Mockito.times;

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
    public void testSuccessFindOwnerById() {
        User user = User.builder()
                .id(1L)
                .build();

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.findOwnerById(user.getId());
        Mockito.verify(userRepository, times(1)).findById(user.getId());

        assertEquals(user, userService.findOwnerById(user.getId()));
    }

    @Test
    public void testFailedFindOwnerById() {
        assertThrows(DataValidationException.class, () -> userService.findOwnerById(0L));
    }

}