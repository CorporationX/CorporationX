package school.faang.user_service.controller.mentorship;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    private final Long USER_ID = 1l;
    private final Long MENTOR_ID = 1L;
    private final Long MENTEE_ID = 2L;
    public User user;
    public UserDTO userDto;
    public List<User> mentees;
    public User first = new User();
    public User second = new User();
    public UserDTO firstDto;
    public UserDTO secondDto;
    public User mentee;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @BeforeEach
    void setUp() {
        user = new User();
        userDto = new UserDTO();
        user.setId(USER_ID);
        mentees = List.of(first, second);
        first.setId(2L);
        second.setId(3L);
        user.setMentees(mentees);
        firstDto = new UserDTO();
        firstDto.setId(2L);
        secondDto = new UserDTO();
        secondDto.setId(3L);
        mentee = new User();
        mentee.setId(MENTEE_ID);
    }

    @Test
    void testGetUserByIdAndRightReturnUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        User result = mentorshipService.getUserById(USER_ID);
        assertEquals(USER_ID, result.getId());
    }

    @Test
    void testGetUserByIdWithException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getUserById(USER_ID));
    }

    @Test
    void testGetMenteesExistingUserReturnsListOfMenteesDTO() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(first)).thenReturn(userDto);
        when(userMapper.toDto(second)).thenReturn(userDto);
        List<UserDTO> actual = mentorshipService.getMentees(USER_ID);
        assertEquals(2, actual.size());
        assertEquals(userDto, actual.get(0));
    }

    @Test
    void testGetMenteesNoMenteesReturnsEmptyList() {
        user.setMentees(Collections.emptyList());
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        List<UserDTO> actual = mentorshipService.getMentees(USER_ID);
        assertTrue(actual.isEmpty());
    }

    @Test
    void testGetMenteesSuccessVerifyMapping() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(first)).thenReturn(firstDto);
        when(userMapper.toDto(second)).thenReturn(secondDto);
        List<UserDTO> actual = mentorshipService.getMentees(USER_ID);
        assertEquals(2, actual.size());
        assertEquals(firstDto, actual.get(0));
        assertEquals(secondDto, actual.get(1));
    }

    @Test
    void testGetMentorsExistingUserReturnsListOfMentorsDTOs() {
        List<User> mentors = List.of(first, second);
        user.setMentors(mentors);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(first)).thenReturn(new UserDTO());
        when(userMapper.toDto(second)).thenReturn(new UserDTO());
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
        when(userMapper.toDto(first)).thenReturn(firstDto);
        when(userMapper.toDto(second)).thenReturn(secondDto);
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
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMenteeMentorNotFoundThrowsException() {
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMenteeNotAMenteeThrowsException() {
        user.setId(MENTOR_ID);
        user.setMentees(new ArrayList<>());
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(first));
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(MENTEE_ID, MENTOR_ID));
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
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMentorNotAMentorThrowsException() {
        mentee.setMentors(new ArrayList<>());
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(first));
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void testDeleteMentorMenteeNotFoundThrowsException() {
        when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(first));
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(MENTEE_ID, MENTOR_ID));
    }
}