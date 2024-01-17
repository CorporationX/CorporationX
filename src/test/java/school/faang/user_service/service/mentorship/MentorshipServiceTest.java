package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;
    private static final long EXISTENT_USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;

    @Test
    public void testGetMentees_UserNotExist_ThrowsEntityNotFoundException() {
        Mockito.when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.getMentees(NON_EXISTENT_USER_ID)
        );
        Mockito.verify(userRepository, Mockito.times(1)).findById(NON_EXISTENT_USER_ID);
    }

    @Test
    public void testGetMentees_UserExistsWithMentees_ReturnsMentees() {
        User user = new User();
        User mentee = new User();
        user.setId(EXISTENT_USER_ID);
        user.setMentees(List.of(mentee));

        UserDto resultDto = new UserDto();

        Mockito.when(userRepository.findById(EXISTENT_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDto(mentee)).thenReturn(resultDto);
        List<UserDto> result = mentorshipService.getMentees(EXISTENT_USER_ID);

        assertEquals(resultDto, result.get(0));
        Mockito.verify(userRepository, Mockito.times(1)).findById(EXISTENT_USER_ID);
        Mockito.verify(userMapper, Mockito.times(1)).toDto(mentee);
    }

    @Test
    public void testGetMentees_UserExistsWithNoMentees_ReturnsEmptyList () {
        User user = new User();
        user.setId(EXISTENT_USER_ID);
        user.setMentees(new ArrayList<>());//no mentees

        Mockito.when(userRepository.findById(EXISTENT_USER_ID)).thenReturn(Optional.of(user));
        List<UserDto> result = mentorshipService.getMentees(EXISTENT_USER_ID);

        assertEquals(0, result.size());
        Mockito.verify(userRepository, Mockito.times(1)).findById(EXISTENT_USER_ID);
        Mockito.verify(userMapper, Mockito.times(1)).listToDto(user.getMentors());
    }
}
