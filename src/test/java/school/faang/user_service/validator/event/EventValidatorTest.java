package school.faang.user_service.validator.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Mock
    private UserService userService;
    @Mock
    private Event event;
    @Mock
    private User user;
    @InjectMocks
    private EventValidator eventValidator;

    @Test
    public void shouldThrowsWhenEvent_NotValidTitleAndDate() {
        EventDto eventEmptyTitle = EventDto.builder()
                .title("")
                .startDate(LocalDateTime.now())
                .ownerId(1L)
                .build();
        EventDto eventNotValidDate = EventDto.builder()
                .title("Event1")
                .startDate(LocalDateTime.now())
                .ownerId(1L)
                .build();

        assertThrows(DataValidationException.class, () -> eventValidator.validateEventInController(eventEmptyTitle));
        assertThrows(DataValidationException.class, () -> eventValidator.validateEventInController(eventNotValidDate));
    }

    @Test
    public void successPassedEventValidationInController() {
        EventDto eventValid = EventDto.builder()
                .title("Event3")
                .startDate(LocalDateTime.now().plusDays(1L))
                .build();
        assertTrue(eventValidator.validateEventInController(eventValid));
    }

    @Test
    void successCheckIfOwnerHasSkillsRequired() {
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();

        Mockito.when(event.getOwner()).thenReturn(user);
        Mockito.when(user.getSkills()).thenReturn(List.of(skill1, skill2));
        Mockito.when(event.getRelatedSkills()).thenReturn(List.of(skill1, skill2));

        assertTrue(eventValidator.checkIfOwnerHasSkillsRequired(event));
    }

    @Test
    public void shouldFailCheckIfOwnerHasSkillsRequired() {
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Skill skill3 = Skill.builder().id(3L).build();

        Mockito.when(event.getOwner()).thenReturn(user);
        Mockito.when(user.getSkills()).thenReturn(List.of(skill1, skill2));
        Mockito.when(event.getRelatedSkills()).thenReturn(List.of(skill3));

        assertThrows(DataValidationException.class,
                () -> eventValidator.checkIfOwnerHasSkillsRequired(event));
    }

    @Test
    public void successCheckIfOwnerExistsById() {
        User user = User.builder()
                .id(1L)
                .active(true)
                .build();

        Mockito.when(userService.checkIfOwnerExistsById(user.getId())).thenReturn(true);
        assertTrue(eventValidator.checkIfOwnerExistsById(user.getId()));
    }

    @Test
    public void shouldThrowCheckIfOwnerExistsById() {
        User user = User.builder()
                .id(1L)
                .active(true)
                .build();
        Mockito.when(userService.checkIfOwnerExistsById(user.getId())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> eventValidator.checkIfOwnerExistsById(user.getId()));
    }

}