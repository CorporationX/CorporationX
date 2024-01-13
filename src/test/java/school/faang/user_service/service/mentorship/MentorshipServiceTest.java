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
        Mockito.when(mentorshipRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.getMentors(NON_EXISTENT_USER_ID)
        );
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(NON_EXISTENT_USER_ID);
    }

    @Test
    public void testGetMentees_EntityFound() {
        User user = new User();
        User mentor = new User();
        user.setId(EXISTENT_USER_ID);
        user.setMentors(List.of(mentor));

        UserDto resultDto = new UserDto();

        Mockito.when(mentorshipRepository.findById(EXISTENT_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDto(mentor)).thenReturn(resultDto);
        List<UserDto> result = mentorshipService.getMentors(EXISTENT_USER_ID);

        assertEquals(resultDto, result.get(0));
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(EXISTENT_USER_ID);
        Mockito.verify(userMapper, Mockito.times(1)).toDto(mentor);
    }
}