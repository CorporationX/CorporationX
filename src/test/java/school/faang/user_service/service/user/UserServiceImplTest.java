package school.faang.user_service.service.user;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final long USER_ID = 1L;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(USER_ID);
    }

    @Test
    public void whenSkillNotExistsByIdThenThrowsException() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        Assert.assertThrows(NoSuchElementException.class,
                () -> userService.existsById(USER_ID));
    }


    @Test
    public void whenSkillExistsByIdThenNoException() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        assertThat(userService.existsById(USER_ID)).isTrue();
    }
}