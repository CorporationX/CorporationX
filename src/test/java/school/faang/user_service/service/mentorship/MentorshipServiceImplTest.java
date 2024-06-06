package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {
    private static final long USER_ID = 1L;
    private static final long MENTOR_ID = 1L;
    private static final long MENTEE_ID = 2L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;
    @Mock
    private GoalService goalService;
    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    private User user;
    private User first;
    private User second;
    private UserDTO userDto;
    private List<User> mentees;
    private UserDTO firstDto;
    private User mentee;

    private GoalDto goalDto;
    private List<GoalDto> goalDtos;

    @BeforeEach
    void setUp() {
        user = new User();
        first = new User();
        second = new User();
        userDto = new UserDTO();
        user.setId(USER_ID);
        first.setId(MENTEE_ID);
        second.setId(3L);
        user.setMentors(List.of(first, second));
        mentees = List.of(first, second);
        user.setMentees(mentees);
        userDto.setMenteeIds(List.of(MENTEE_ID));
        firstDto = new UserDTO();
        firstDto.setId(MENTEE_ID);
        firstDto.setMentorIds(List.of(MENTOR_ID));
        mentee = new User();
        mentee.setId(MENTEE_ID);
        goalDto = new GoalDto();
        goalDto.setMentorId(MENTOR_ID);
        goalDtos = List.of(goalDto);
    }

    @Test
    void testGetMenteesExistingUserReturnsListOfMenteesDTO() {
        when(userService.findById(USER_ID)).thenReturn(userDto);
        when(userService.findById(MENTEE_ID)).thenReturn(firstDto);
        List<UserDTO> actual = mentorshipService.getMentees(USER_ID);
        assertEquals(1, actual.size());
        assertEquals(firstDto, actual.get(0));
    }

    @Test
    void testGetMenteesNoMenteesReturnsEmptyList() {
        userDto.setMenteeIds(Collections.emptyList());
        when(userService.findById(USER_ID)).thenReturn(userDto);
        List<UserDTO> actual = mentorshipService.getMentees(USER_ID);
        assertTrue(actual.isEmpty());
    }

    @Test
    void testGetMentorsExistingUserReturnsListOfMentorsDTOs() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(first)).thenReturn(new UserDTO());
        when(userMapper.toDTO(second)).thenReturn(new UserDTO());
        List<UserDTO> actual = mentorshipService.getMentors(USER_ID);
        assertEquals(2, actual.size());
    }

    @Test
    void testGetMentorsNoMentorsReturnsEmptyList() {
        user.setMentors(Collections.emptyList());
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        List<UserDTO> actual = mentorshipService.getMentors(USER_ID);
        assertTrue(actual.isEmpty());
    }

    @Test
    void testGetMentorsSuccessVerifyMapping() {
        List<User> mentors = List.of(first, second);
        user.setMentors(mentors);
        UserDTO firstDto = new UserDTO();
        firstDto.setId(2L);
        UserDTO secondDto = new UserDTO();
        secondDto.setId(3L);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(first)).thenReturn(firstDto);
        when(userMapper.toDTO(second)).thenReturn(secondDto);
        List<UserDTO> actual = mentorshipService.getMentors(USER_ID);
        assertEquals(2, actual.size());
        assertEquals(firstDto, actual.get(0));
        assertEquals(secondDto, actual.get(1));
    }

    @Test
    void testDeleteMenteeSuccess() {
        user.setId(MENTOR_ID);
        user.setMentees(new ArrayList<>(List.of(first)));
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(first));
        mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID);
        verify(userRepository).save(user);
        assertFalse(user.getMentees().contains(first));
    }

    @Test
    void testDeleteMenteeMenteeNotFoundThrowsException() {
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMenteeMentorNotFoundThrowsException() {
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMenteeNotAMenteeThrowsException() {
        user.setId(MENTOR_ID);
        user.setMentees(new ArrayList<>());
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(first));
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMentorSuccess() {
        mentee.setMentors(new ArrayList<>(List.of(first)));
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(first));
        mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID);
        verify(userRepository).save(mentee);
        assertFalse(mentee.getMentors().contains(first));
    }

    @Test
    void testDeleteMentorMentorNotFoundThrowsException() {
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMentorNotAMentorThrowsException() {
        mentee.setMentors(new ArrayList<>());
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(first));
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMentorMenteeNotFoundThrowsException() {
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(first));
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID));
    }

    @Test
    public void whenStopMentorshipSuccessfully() {
        when(userService.findById(MENTOR_ID)).thenReturn(userDto);
        when(userService.findById(MENTEE_ID)).thenReturn(firstDto);
        when(goalService.findGoalsByUserId(MENTEE_ID)).thenReturn(goalDtos);
        mentorshipService.stopMentorship(MENTOR_ID);
        verify(userService).update(firstDto);
    }
}