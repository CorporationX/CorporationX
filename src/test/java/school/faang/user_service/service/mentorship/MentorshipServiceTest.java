package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.handlers.ControllerError;
import school.faang.user_service.handlers.GlobalExceptionHandler;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private static final long EXISTENT_USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;
    private static final long EXISTENT_MENTEE_ID = 1L;
    private static final long EXISTENT_MENTOR_ID = 2L;
    private User mentor;
    private User mentee;

    @BeforeEach
    public void setUp() {
        mentor = User.builder().id(EXISTENT_MENTOR_ID).build();
        mentee = User.builder().id(EXISTENT_MENTEE_ID).build();
        User anotherMentee = User.builder().id(3L).build();
        User anotherMentor = User.builder().id(4L).build();
        mentor.setMentees(new ArrayList<>(List.of(mentee, anotherMentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor, anotherMentor)));
    }

    @Test
    public void testGetMentors_UserNotExist_ThrowsEntityNotFoundException() {
        Mockito.when(userService.getExistingUserById(NON_EXISTENT_USER_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.getMentors(NON_EXISTENT_USER_ID)
        );

        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(NON_EXISTENT_USER_ID);
    }

    @Test
    public void testGetMentees_UserNotExist_ThrowsEntityNotFoundException() {
        Mockito.when(userService.getExistingUserById(NON_EXISTENT_USER_ID)).thenThrow(EntityNotFoundException.class);

        assertEntityNotFoundExceptionAndNotFoundResponse(
                () -> mentorshipService.getMentees(NON_EXISTENT_USER_ID)
        );

        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(NON_EXISTENT_USER_ID);
    }

    @Test
    public void testGetMentors_UserExistsWithMentors_ReturnsMentors() {
        User mentor = new User();
        List<User> usersMentors = List.of(mentor);

        User user = new User();
        user.setId(EXISTENT_USER_ID);
        user.setMentors(usersMentors);

        List<UserDto> resultMentorsDtos = List.of(new UserDto());

        Mockito.when(userService.getExistingUserById(EXISTENT_USER_ID)).thenReturn(user);
        Mockito.when(userMapper.listToDto(usersMentors)).thenReturn(resultMentorsDtos);

        List<UserDto> result = mentorshipService.getMentors(EXISTENT_USER_ID);

        assertEquals(resultMentorsDtos, result);
        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_USER_ID);
        Mockito.verify(userMapper, Mockito.times(1)).listToDto(usersMentors);
    }

    @Test
    public void testGetMentees_UserExistsWithMentees_ReturnsMentees() {
        List<User> userMentees = List.of(new User());

        User user = new User();
        user.setId(EXISTENT_USER_ID);
        user.setMentees(userMentees);

        List<UserDto> resultMenteeDtos = List.of(new UserDto());

        Mockito.when(userService.getExistingUserById(EXISTENT_USER_ID)).thenReturn(user);
        Mockito.when(userMapper.listToDto(userMentees)).thenReturn(resultMenteeDtos);

        List<UserDto> result = mentorshipService.getMentees(EXISTENT_USER_ID);

        assertEquals(resultMenteeDtos, result);
        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_USER_ID);
        Mockito.verify(userMapper, Mockito.times(1)).listToDto(userMentees);
    }

    @Test
    public void testGetMentors_UserExistsWithNoMentors_ReturnsEmptyList() {
        User user = new User();
        user.setId(EXISTENT_USER_ID);
        user.setMentors(new ArrayList<>());//no mentors

        Mockito.when(userService.getExistingUserById(EXISTENT_USER_ID)).thenReturn(user);
        List<UserDto> result = mentorshipService.getMentors(EXISTENT_USER_ID);

        assertEquals(0, result.size());
        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_USER_ID);
        Mockito.verify(userMapper, Mockito.times(1)).listToDto(user.getMentors());
    }

    @Test
    public void testGetMentees_UserExistsWithNoMentees_ReturnsEmptyList() {
        User user = new User();
        user.setId(EXISTENT_USER_ID);
        user.setMentees(new ArrayList<>());//no mentees

        Mockito.when(userService.getExistingUserById(EXISTENT_USER_ID)).thenReturn(user);
        List<UserDto> result = mentorshipService.getMentees(EXISTENT_USER_ID);

        assertEquals(0, result.size());
        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_USER_ID);
        Mockito.verify(userMapper, Mockito.times(1)).listToDto(user.getMentees());
    }

    @Test
    public void testDeleteMentor_AnyUserNotExist_EntityNotFoundException() {
        Mockito.when(userService.getExistingUserById(NON_EXISTENT_USER_ID)).thenThrow(EntityNotFoundException.class);
        Mockito.when(userService.getExistingUserById(EXISTENT_USER_ID)).thenReturn(new User());

        assertEntityNotFoundExceptionAndNotFoundResponse(
                () -> mentorshipService.deleteMentee(NON_EXISTENT_USER_ID, NON_EXISTENT_USER_ID)
        );
        assertEntityNotFoundExceptionAndNotFoundResponse(
                () -> mentorshipService.deleteMentee(EXISTENT_USER_ID, NON_EXISTENT_USER_ID)
        );
        assertEntityNotFoundExceptionAndNotFoundResponse(
                () -> mentorshipService.deleteMentee(NON_EXISTENT_USER_ID, EXISTENT_USER_ID)
        );

        Mockito.verify(mentorshipRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testDeleteMentee_AnyUserNotExist_EntityNotFoundException() {
        Mockito.when(userService.getExistingUserById(NON_EXISTENT_USER_ID)).thenThrow(EntityNotFoundException.class);
        Mockito.when(userService.getExistingUserById(EXISTENT_USER_ID)).thenReturn(new User());

        assertEntityNotFoundExceptionAndNotFoundResponse(
                () -> mentorshipService.deleteMentee(NON_EXISTENT_USER_ID, NON_EXISTENT_USER_ID)
        );
        assertEntityNotFoundExceptionAndNotFoundResponse(
                () -> mentorshipService.deleteMentee(EXISTENT_USER_ID, NON_EXISTENT_USER_ID)
        );
        assertEntityNotFoundExceptionAndNotFoundResponse(
                () -> mentorshipService.deleteMentee(NON_EXISTENT_USER_ID, EXISTENT_USER_ID)
        );

        Mockito.verify(mentorshipRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    public void testDeleteMentor_UsersExistsWithMentorship_MentorDeletedAndMenteeSaved() {
        Mockito.when(userService.getExistingUserById(EXISTENT_MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userService.getExistingUserById(EXISTENT_MENTEE_ID)).thenReturn(mentee);

        List<User> mentorsOfMentee = mentee.getMentors();
        int mentorsBeforeRemovalCount = mentorsOfMentee.size();

        mentorshipService.deleteMentor(EXISTENT_MENTEE_ID, EXISTENT_MENTOR_ID);

        int mentorsAfterRemovalCount = mentorsOfMentee.size();

        assertEquals(2, mentorsBeforeRemovalCount);
        assertEquals(1, mentorsAfterRemovalCount);
        assertFalse(mentorsOfMentee.contains(mentor));

        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_MENTOR_ID);
        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_MENTEE_ID);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentee);
    }

    @Test
    public void testDeleteMentee_UsersExistsWithMentorship_MenteeDeletedAndMentorSaved() {
        Mockito.when(userService.getExistingUserById(EXISTENT_MENTEE_ID)).thenReturn(mentee);
        Mockito.when(userService.getExistingUserById(EXISTENT_MENTOR_ID)).thenReturn(mentor);

        List<User> menteesOfMentor = mentor.getMentees();
        int menteesBeforeRemovalCount = menteesOfMentor.size();

        mentorshipService.deleteMentee(EXISTENT_MENTOR_ID, EXISTENT_MENTEE_ID);

        int menteesAfterRemovalCount = menteesOfMentor.size();

        assertEquals(2, menteesBeforeRemovalCount);
        assertEquals(1, menteesAfterRemovalCount);
        assertFalse(menteesOfMentor.contains(mentee));

        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_MENTEE_ID);
        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_MENTOR_ID);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentor);
    }

    @Test
    public void testDeleteMentor_UsersExistsWithNoMentorship_MentorNotDeletedAndMenteeNotSaved() {
        mentee.getMentors().remove(mentor);
        //mentee NOW do not have deleting mentor

        Mockito.when(userService.getExistingUserById(EXISTENT_MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userService.getExistingUserById(EXISTENT_MENTEE_ID)).thenReturn(mentee);

        List<User> mentorsOfMentee = mentee.getMentors();
        int mentorsBeforeRemovalCount = mentorsOfMentee.size();

        mentorshipService.deleteMentor(EXISTENT_MENTEE_ID, EXISTENT_MENTOR_ID);

        int mentorsAfterRemovalCount = mentorsOfMentee.size();

        assertEquals(1, mentorsBeforeRemovalCount);
        assertEquals(1, mentorsAfterRemovalCount);
        assertFalse(mentorsOfMentee.contains(mentor));

        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_MENTOR_ID);
        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_MENTEE_ID);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(mentee);
    }

    @Test
    public void testDeleteMentee_UsersExistsWithNoMentorship_MenteeNotDeletedAndMentorNotSaved() {
        mentor.getMentees().remove(mentee);
        //mentor NOW do not have deleting mentee

        Mockito.when(userService.getExistingUserById(EXISTENT_MENTEE_ID)).thenReturn(mentee);
        Mockito.when(userService.getExistingUserById(EXISTENT_MENTOR_ID)).thenReturn(mentor);

        List<User> menteesOfMentor = mentor.getMentees();
        int menteesBeforeRemovalCount = menteesOfMentor.size();

        mentorshipService.deleteMentee(EXISTENT_MENTOR_ID, EXISTENT_MENTEE_ID);

        int menteesAfterRemovalCount = menteesOfMentor.size();

        assertEquals(1, menteesBeforeRemovalCount);
        assertEquals(1, menteesAfterRemovalCount);
        assertFalse(menteesOfMentor.contains(mentee));

        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_MENTEE_ID);
        Mockito.verify(userService, Mockito.times(1)).getExistingUserById(EXISTENT_MENTOR_ID);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(mentor);
    }

    private void assertEntityNotFoundExceptionAndNotFoundResponse(Executable method) {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                method
        );
        ResponseEntity<ControllerError> responseEntity = exceptionHandler.handleEntityNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}