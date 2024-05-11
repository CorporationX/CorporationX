package school.faang.user_service.controller.mentorship;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.service.mentorship.MentorshipService;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    @Test
    void testGetMentees() {
        long mentorId = 1L;
        List<UserDTO> expectedMentees = new ArrayList<>();
        when(mentorshipService.getMentees(mentorId)).thenReturn(expectedMentees);
        List<UserDTO> actualMentees = mentorshipController.getMentees(mentorId);
        assertEquals(expectedMentees, actualMentees);
    }


    @Test
    void testGetMentors() {
        long userId = 1L;
        List<UserDTO> expectedMentors = new ArrayList<>();
        when(mentorshipService.getMentors(userId)).thenReturn(expectedMentors);
        List<UserDTO> actualMentors = mentorshipController.getMentors(userId);
        assertEquals(expectedMentors, actualMentors);
    }

    @Test
    void testDeleteMentee() {
        long mentorId = 1;
        long menteeId = 2;
        mentorshipController.deleteMentee(mentorId, menteeId);
        verify(mentorshipService, times(1)).deleteMentee(menteeId, mentorId);
    }

    @Test
    void testDeleteMentor() {
        long menteeId = 1;
        long mentorId = 2;
        mentorshipController.deleteMentor(menteeId, mentorId);
        verify(mentorshipService, times(1)).deleteMentor(menteeId, mentorId);
    }
}