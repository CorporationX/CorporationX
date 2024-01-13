package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    private static final long EXISTENT_USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteMentee_EntityNotFoundException() {
        Mockito.when(mentorshipRepository.findById(Mockito.eq(NON_EXISTENT_USER_ID))).thenReturn(Optional.empty());
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(NON_EXISTENT_USER_ID, NON_EXISTENT_USER_ID)
        );
    }

    @Test
    public void testGetMentees_EntityFound() {
        User mentorByMentee = Mockito.mock(User.class);
        User mentor = mentorByMentee;
        User mentee = Mockito.mock(User.class);

        Mockito.when(mentorshipRepository.findById(Mockito.eq(EXISTENT_USER_ID))).thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipRepository.findById(Mockito.eq(EXISTENT_USER_ID))).thenReturn(Optional.of(mentor));

        Mockito.when(mentee.getMentors()).thenReturn(List.of(mentorByMentee));


        List<UserDto> resultMentees = mentorshipService.getMentees(EXISTENT_USER_ID);

        assertEquals(menteeDto, resultMentees.get(0));
    }
}