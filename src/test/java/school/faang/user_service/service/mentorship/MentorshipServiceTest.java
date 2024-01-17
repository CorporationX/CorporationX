package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    private static final long EXISTENT_MENTEE_ID = 1L;
    private static final long EXISTENT_MENTOR_ID = 2L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;
    private User mentor;
    private User mentee;

    @BeforeEach
    public void setUp() {
        mentor = User.builder().id(EXISTENT_MENTOR_ID).build();
        mentee = User.builder().id(EXISTENT_MENTEE_ID).mentors(List.of(mentor)).build();
        User anotherMentee = User.builder().id(3L).build();
        mentor.setMentees(new ArrayList<>(List.of(mentee, anotherMentee)));
    }

    @Test
    public void testDeleteMentee_AnyUserNotExist_EntityNotFoundException() {
        Mockito.when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(NON_EXISTENT_USER_ID, NON_EXISTENT_USER_ID)
        );
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(EXISTENT_MENTOR_ID, NON_EXISTENT_USER_ID)
        );
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(NON_EXISTENT_USER_ID, EXISTENT_MENTEE_ID)
        );
    }

    @Test
    public void testDeleteMentee_UsersExistsWithMentorship_MenteeDeletedAndMentorSaved() {
        Mockito.when(userRepository.findById(EXISTENT_MENTEE_ID)).thenReturn(Optional.of(mentee));
        Mockito.when(userRepository.findById(EXISTENT_MENTOR_ID)).thenReturn(Optional.of(mentor));

        List<User> menteesOfMentor = mentor.getMentees();
        int menteesBeforeRemovalCount = menteesOfMentor.size();

        mentorshipService.deleteMentee(EXISTENT_MENTOR_ID, EXISTENT_MENTEE_ID);

        int menteesAfterRemovalCount = menteesOfMentor.size();

        assertEquals(2, menteesBeforeRemovalCount);
        assertEquals(1, menteesAfterRemovalCount);
        assertFalse(menteesOfMentor.contains(mentee));

        Mockito.verify(userRepository, Mockito.times(1)).findById(EXISTENT_MENTEE_ID);
        Mockito.verify(userRepository, Mockito.times(1)).findById(EXISTENT_MENTOR_ID);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentor);
    }

    @Test
    public void testDeleteMentee_UsersExistsWithNoMentorship_MenteeNotDeletedAndMentorNotSaved() {
        mentor.getMentees().remove(mentee);
        //mentor NOW do not have deleting mentee

        Mockito.when(userRepository.findById(EXISTENT_MENTEE_ID)).thenReturn(Optional.of(mentee));
        Mockito.when(userRepository.findById(EXISTENT_MENTOR_ID)).thenReturn(Optional.of(mentor));

        List<User> menteesOfMentor = mentor.getMentees();
        int menteesBeforeRemovalCount = menteesOfMentor.size();

        mentorshipService.deleteMentee(EXISTENT_MENTOR_ID, EXISTENT_MENTEE_ID);

        int menteesAfterRemovalCount = menteesOfMentor.size();

        assertEquals(1, menteesBeforeRemovalCount);
        assertEquals(1, menteesAfterRemovalCount);
        assertFalse(menteesOfMentor.contains(mentee));

        Mockito.verify(userRepository, Mockito.times(1)).findById(EXISTENT_MENTEE_ID);
        Mockito.verify(userRepository, Mockito.times(1)).findById(EXISTENT_MENTOR_ID);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(mentor);
    }
}
