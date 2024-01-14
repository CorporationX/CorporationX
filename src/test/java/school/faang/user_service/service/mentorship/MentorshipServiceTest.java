package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    private static final long EXISTENT_MENTEE_ID = 1L;
    private static final long EXISTENT_MENTOR_ID = 2L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteMentee_EntityNotFoundException() {
        Mockito.when(mentorshipRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());
        Mockito.when(mentorshipRepository.findById(EXISTENT_MENTOR_ID)).thenReturn(Optional.of(new User()));
        Mockito.when(mentorshipRepository.findById(EXISTENT_MENTEE_ID)).thenReturn(Optional.of(new User()));

        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(NON_EXISTENT_USER_ID, NON_EXISTENT_USER_ID)
        );
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(EXISTENT_MENTOR_ID, NON_EXISTENT_USER_ID)
        );
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(NON_EXISTENT_USER_ID, EXISTENT_MENTEE_ID)
        );
    }

    @Test
    public void testGetMentees_EntityFound() {

        User mentorToDelete = new User();
        User mentor = new User();
        User mentee = new User();

        mentorToDelete.setId(5L);
        mentor.setId(6L);//for the equals and remove method to work correctly
        mentorToDelete.setMentees(List.of(mentee));
        mentee.setMentors(new ArrayList<>(List.of(mentor, mentorToDelete)));

        Mockito.when(mentorshipRepository.findById(EXISTENT_MENTEE_ID)).thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipRepository.findById(EXISTENT_MENTOR_ID)).thenReturn(Optional.of(mentorToDelete));

        List<User> mentorsOfMentee = mentee.getMentors();
        int mentorsBeforeRemovalCount = mentorsOfMentee.size();

        mentorshipService.deleteMentor(EXISTENT_MENTEE_ID, EXISTENT_MENTOR_ID);

        int mentorsAfterRemovalCount = mentorsOfMentee.size();

        assertEquals(2, mentorsBeforeRemovalCount);
        assertEquals(1, mentorsAfterRemovalCount);
        assertFalse(mentorsOfMentee.contains(mentorToDelete));
        assertTrue(mentorsOfMentee.contains(mentor));

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(EXISTENT_MENTEE_ID);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(EXISTENT_MENTOR_ID);
    }
}