package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {
    private static final long MENTOR_ID = 1L;
    private static final long MENTEE_ID = 2L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private GoalService goalService;
    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    private User mentor;
    private User mentee;

    private UserDTO mentorDto;
    private UserDTO menteeDto;
    private GoalDto goalDto;
    private List<GoalDto> goalDtos;

    @BeforeEach
    void setUp() {
        mentor = new User();
        mentee = new User();
        mentorDto = new UserDTO();
        menteeDto = new UserDTO();
        mentor.setId(MENTOR_ID);
        mentee.setId(MENTEE_ID);
        mentorDto.setId(MENTOR_ID);
        menteeDto.setId(MENTEE_ID);

        List<User> mentees = new ArrayList<>();
        List<User> mentors = new ArrayList<>();
        List<Long> menteeIds = new ArrayList<>();
        List<Long> mentorIds = new ArrayList<>();
        mentees.add(mentee);
        mentors.add(mentor);
        menteeIds.add(MENTEE_ID);
        mentorIds.add(MENTOR_ID);

        mentor.setMentees(mentees);
        mentor.setMentors(Collections.emptyList());
        mentee.setMentors(mentors);
        mentee.setMentees(Collections.emptyList());

        mentorDto.setMenteeIds(menteeIds);
        mentorDto.setMentorIds(Collections.emptyList());
        menteeDto.setMentorIds(mentorIds);
        menteeDto.setMenteeIds(Collections.emptyList());
        goalDto = new GoalDto();
        goalDto.setMentorId(MENTOR_ID);
        goalDtos = List.of(goalDto);
    }

    @Test
    void testGetMenteesExistingUserReturnsListOfMenteesDTO() {
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        when(userMapper.toDTO(mentor)).thenReturn(mentorDto);
        when(userMapper.toDTO(mentee)).thenReturn(menteeDto);
        List<UserDTO> actual = mentorshipService.getMentees(MENTOR_ID);
        assertEquals(1, actual.size());
        assertEquals(menteeDto, actual.get(0));
    }

    @Test
    void testGetMenteesNoMenteesReturnsEmptyList() {
        mentorDto.setMenteeIds(Collections.emptyList());
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        when(userMapper.toDTO(mentor)).thenReturn(mentorDto);
        List<UserDTO> actual = mentorshipService.getMentees(MENTOR_ID);
        assertTrue(actual.isEmpty());
    }

    @Test
    void testGetMentorsExistingUserReturnsListOfMentorsDTOs() {
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        when(userMapper.toDTO(mentor)).thenReturn(mentorDto);
        List<UserDTO> actual = mentorshipService.getMentors(MENTEE_ID);
        assertEquals(1, actual.size());
        assertEquals(mentorDto, actual.get(0));
    }

    @Test
    void testGetMentorsNoMentorsReturnsEmptyList() {
        mentee.setMentors(Collections.emptyList());
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        List<UserDTO> actual = mentorshipService.getMentors(MENTEE_ID);
        assertTrue(actual.isEmpty());
    }

    @Test
    void testDeleteMenteeSuccess() {
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID);
        verify(userRepository).save(mentor);
        assertFalse(mentor.getMentees().contains(mentee));
    }

    @Test
    void testDeleteMenteeMenteeNotFoundThrowsException() {
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
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
        mentor.setMentees(new ArrayList<>());
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMentorSuccess() {
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID);
        verify(userRepository).save(mentee);
        assertFalse(mentee.getMentors().contains(mentor));
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
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMentorMenteeNotFoundThrowsException() {
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID));
    }

    @Test
    public void whenStopMentorshipSuccessfully() {
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        when(userMapper.toDTO(mentor)).thenReturn(mentorDto);
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        when(userMapper.toDTO(mentee)).thenReturn(menteeDto);
        when(goalService.findGoalsByUserId(MENTEE_ID)).thenReturn(goalDtos);
        mentorshipService.stopMentorship(MENTOR_ID);
        verify(userRepository).save(mentee);
    }

    @Test
    public void whenAssignMentorSuccessfully() {
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        mentorshipService.assignMentor(MENTEE_ID, MENTOR_ID);
        verify(userRepository).save(mentee);
        verify(userRepository).save(mentor);
    }
}