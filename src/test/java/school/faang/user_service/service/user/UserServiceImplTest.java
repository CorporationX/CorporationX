package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.profile_picture.ProfilePictureService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDTO userDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(USER_ID);
        userDto = new UserDTO();
        userDto.setId(USER_ID);
    }

    @Test
    public void whenUserNotExistsByIdThenThrowsException() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        Assert.assertThrows(NoSuchElementException.class,
                () -> userService.existsById(USER_ID));
    }

    @Test
    public void whenUserExistsByIdThenNoException() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        assertThat(userService.existsById(USER_ID)).isTrue();
    }

    @Test
    public void whenCreateUserThenGetUserDto() {
        when(userMapper.toEntity(any())).thenReturn(user);
        when(userMapper.toDTO(any())).thenReturn(userDto);
        when(userRepository.save(any())).thenReturn(user);
        UserDTO actual = userService.createUser(userDto);
        assertThat(actual).isEqualTo(userDto);
    }

    @Test
    public void whenFindByIdAndUserNotExistsThenThrowsException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class,
                () -> userService.findById(USER_ID));
    }

    @Test
    public void whenFindByIdAndUserThenReturnUserDto() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDto);
        UserDTO actual = userService.findById(USER_ID);
        assertThat(actual).isEqualTo(userDto);
    }

    @Test
    public void whenUpdateSuccessfullyThenReturnUserDto() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        UserDTO actual = userService.update(userDto);
        assertThat(actual).isEqualTo(userDto);
    }

    @Test
    public void whenDeactivateUserSuccessfully() {
        Goal goal = new Goal();
        Event event = new Event();
        goal.setId(3L);
        event.setId(2L);
        goal.setUsers(List.of(user));
        event.setOwner(user);
        user.setGoals(List.of(goal));
        user.setOwnedEvents(List.of(event));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        userService.deactivateUser(USER_ID);
        verify(mentorshipService).stopMentorship(USER_ID);
    }
}