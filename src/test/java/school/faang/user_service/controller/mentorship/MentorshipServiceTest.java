package school.faang.user_service.controller.mentorship;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;



    @Test
    void testGetMenteesForMentorWithNoMentees() {
        long userId = 1L;
        User mentor = new User();
        mentor.setId(userId);
        mentor.setMentees(new ArrayList<>());
        when(userRepository.findById(userId)).thenReturn(Optional.of(mentor));
        List<UserDTO> result = mentorshipService.getMentees(userId);
        assertThat(result).isEmpty();
    }


    @Test
    void testGetMenteesWhenMentorNotFound() {
        long nonExistentMentorId = 100L;
        when(mentorshipService.getMentees(nonExistentMentorId))
                .thenThrow(new MentorshipException("Ментор с id: " + nonExistentMentorId + " не найден"));
    }

    @Test
    void testGetMenteesWhenMentorFound() {
        long mentorId = 1L;
        UserDTO menteeDTO = new UserDTO();
        menteeDTO.setId(2L);
        List<UserDTO> expectedMentees = Collections.singletonList(menteeDTO);
        when(mentorshipService.getMentees(mentorId)).thenReturn(expectedMentees);
        List<UserDTO> actualMentees = mentorshipController.getMentees(mentorId);
        assertEquals(expectedMentees.size(), actualMentees.size());
        assertEquals(expectedMentees.get(0).getId(), actualMentees.get(0).getId());
    }
    @Test
    public void testGetMenteesReturnsExpectedList() {
        long mentorId = 1L;
        User mentor = new User();
        mentor.setId(mentorId);
        UserDTO mentee1 = new UserDTO();
        mentee1.setId(2L);
        UserDTO mentee2 = new UserDTO();
        mentee2.setId(3L);
        List<UserDTO> expectedMentees = Arrays.asList(mentee1, mentee2);
        when(mentorshipService.getMentees(mentorId)).thenReturn(expectedMentees);
        List<UserDTO> actualMentees = mentorshipController.getMentees(mentorId);
        assertEquals(expectedMentees, actualMentees);
    }

    @Test
    public void testGetMenteesWithEmptyList() {
        long mentorId = 1L;
        when(mentorshipService.getMentees(mentorId)).thenReturn(List.of());
        List<UserDTO> actualMentees = mentorshipController.getMentees(mentorId);
        assertTrue(actualMentees.isEmpty());
    }

    @Test
    public void testGetMentorsReturnsExpectedList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        UserDTO mentor1 = new UserDTO();
        mentor1.setId(2L);
        UserDTO mentor2 = new UserDTO();
        mentor2.setId(3L);
        List<UserDTO> expectedMentors = Arrays.asList(mentor1, mentor2);
        when(mentorshipService.getMentors(userId)).thenReturn(expectedMentors);
        List<UserDTO> actualMentors = mentorshipController.getMentors(userId);
        assertEquals(expectedMentors, actualMentors);
    }

    @Test
    public void testGetMentorsHandlesException() {
        long invalidUserId = -1L;
        when(mentorshipService.getMentors(invalidUserId))
                .thenThrow(new MentorshipException("Пользователь с id: " + invalidUserId + " не найден"));
        assertThrows(MentorshipException.class, () -> mentorshipController.getMentors(invalidUserId));
    }

    @Test
    public void testGetMentorsWithEmptyList() {
        long userId = 1L;
        when(mentorshipService.getMentors(userId)).thenReturn(List.of());
        List<UserDTO> actualMentors = mentorshipController.getMentors(userId);
        assertTrue(actualMentors.isEmpty());
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

    @Test
    void testDeleteMentorMentorOrMenteeNotFound() {
        long menteeId = 1;
        long mentorId = 2;
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.empty());
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.empty());
        mentorshipService.deleteMentor(menteeId, mentorId);
        verify(mentorshipRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteMentorMenteeDoesNotHaveMentor() {
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