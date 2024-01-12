package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;
    private static final long EXISTENT_USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMentees_EntityNotFoundException() {
        Mockito.when(mentorshipRepository.findById(Mockito.eq(NON_EXISTENT_USER_ID))).thenReturn(Optional.empty());
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.getMentees(NON_EXISTENT_USER_ID)
        );
    }

    @Test
    public void testGetMentees_EntityFound() {
        User userEntity = Mockito.mock(User.class);
        User menteeEntity = Mockito.mock(User.class);
        UserDto menteeDto = Mockito.mock(UserDto.class);

        Mockito.when(mentorshipRepository.findById(Mockito.eq(EXISTENT_USER_ID))).thenReturn(Optional.of(userEntity));
        Mockito.when(userEntity.getMentees()).thenReturn(List.of(menteeEntity));
        Mockito.when(userMapper.toDto(menteeEntity)).thenReturn(menteeDto);

        List<UserDto> resultMentees = mentorshipService.getMentees(EXISTENT_USER_ID);

        assertEquals(menteeDto, resultMentees.get(0));
    }
}