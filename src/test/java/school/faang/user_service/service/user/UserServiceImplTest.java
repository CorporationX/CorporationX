package school.faang.user_service.service.user;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.profile_picture.ProfilePictureService;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final long USER_ID = 1L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfilePictureService profilePictureService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(USER_ID);
        userDto = new UserDto();
        userDto.setId(USER_ID);
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

    @Test
    public void whenCreateUserThenGetUserDto() {
        when(userMapper.toEntity(any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userDto);
        when(userRepository.save(any())).thenReturn(user);
        UserDto actual = userService.createUser(userDto);
        assertThat(actual).isEqualTo(userDto);
    }
}