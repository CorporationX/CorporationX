package school.faang.user_service.controller.mentorship;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMenteesForMentorWithNoMentees() {
        long userId = 1L;
        User mentor = new User();
        mentor.setId(userId);
        List<User> mentees = new ArrayList<>();
        mentor.setMentees(mentees);
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(mentor));
        List<UserDTO> result = mentorshipService.getMentees(userId);
        assertEquals(mentees.size(), result.size());
    }

    @Test
    void testGetMenteesWhenMentorNotFound() {
        long userId = 2L;
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.empty());
        List<UserDTO> result = mentorshipService.getMentees(userId);
        assertEquals(0, result.size());
    }

    @Test
    void testGetMenteesWithNonMentorId() {
        long nonMentorId = 5L;
        when(mentorshipRepository.findById(nonMentorId)).thenReturn(Optional.empty());

        List<UserDTO> result = mentorshipService.getMentees(nonMentorId);
        assertEquals(0, result.size());
    }

//==================================================================================================================

    @Test
    void testGetMentors_ReturnsListOfMentorsForUser() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<User> mentors = new ArrayList<>();
        user.setMentors(mentors);
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(user));
        List<UserDTO> result = mentorshipService.getMentors(userId);
        assertEquals(mentors.size(), result.size());
    }

    @Test
    void testGetMentors_UserNotFound() {
        long userId = 1L;
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.empty());
        List<UserDTO> result = mentorshipService.getMentors(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMentors_UserHasNoMentors() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentors(new ArrayList<>());
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(user));
        List<UserDTO> result = mentorshipService.getMentors(userId);
        assertTrue(result.isEmpty());
    }


//================================================================================================================

    @Test
    void testDeleteMentee_RemovingMenteeFromMentorList() {
        long mentorId = 1L;
        long menteeId = 2L;
        User mentor = new User();
        mentor.setId(mentorId);
        User mentee = new User();
        mentee.setId(menteeId);
        mentor.setMentees(new ArrayList<>(Collections.singletonList(mentee)));
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentee(menteeId, mentorId);
        verify(mentorshipRepository, times(1)).save(mentor);
        assertEquals(0, mentor.getMentees().size());
    }

    @Test
    void testDeleteMenteeWhenMentorNotFound() {
        long mentorId = 1L;
        long menteeId = 2L;
        User mentee = new User();
        mentee.setId(menteeId);
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.empty());
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentee(menteeId, mentorId);
        verify(mentorshipRepository, times(0)).save(mentee);
    }

    @Test
    void testDeleteMenteeWhenMenteeNotFound() {
        long mentorId = 1L;
        long menteeId = 2L;
        User mentor = new User();
        mentor.setId(mentorId);
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.empty());
        mentorshipService.deleteMentee(menteeId, mentorId);
        verify(mentorshipRepository, times(0)).save(mentor);
    }

//==============================================================================================================

    @Test
    void testDeleteMentor_RemovesMentorFromMentee() {
        long menteeId = 1;
        long mentorId = 2;
        User mentor = new User();
        User mentee = new User();
        mentor.setMentees(new ArrayList<>());
        mentee.setMentors(new ArrayList<>());
        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentor(menteeId, mentorId);
        assertFalse(mentee.getMentors().contains(mentor));
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    @Test
    void testDeleteMentor_MentorOrMenteeNotFound() {
        long menteeId = 1;
        long mentorId = 2;
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.empty());
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.empty());
        mentorshipService.deleteMentor(menteeId, mentorId);
        verify(mentorshipRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteMentor_MenteeDoesNotHaveMentor() {
        long menteeId = 1;
        long mentorId = 2;
        User mentor = new User();
        User mentee = new User();
        mentee.setMentors(new ArrayList<>());
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentor(menteeId, mentorId);
        verify(mentorshipRepository, never()).save(any(User.class));
    }
}