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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Mock
    private UserService userService;
    @Mock
    private Event eventFirst;
    @Mock
    private User user;
    @InjectMocks
    private EventValidator eventValidator;

    @Test
    public void shouldThrowsWhenEvent_NotValidTitle() {
        EventDto eventEmptyTitle = EventDto.builder()
                .title("")
                .startDate(LocalDateTime.now())
                .ownerId(1L)
                .build();
        assertThrows(DataValidationException.class, () -> eventValidator.validateEventInController(eventEmptyTitle));
    }

    @Test
    public void shouldThrowsWhenEventTitleIsBlank() {
        EventDto eventEmptyTitle = EventDto.builder()
                .title("      ")
                .startDate(LocalDateTime.now())
                .ownerId(1L)
                .build();
        assertThrows(DataValidationException.class, () -> eventValidator.validateEventInController(eventEmptyTitle));
    }

    @Test
    public void shouldThrowsWhenEventNotValidDate() {
        EventDto eventNotValidDate = EventDto.builder()
                .title("Event1")
                .startDate(LocalDateTime.now().minusDays(1L))
                .ownerId(1L)
                .build();
        assertThrows(DataValidationException.class, () -> eventValidator.validateEventInController(eventNotValidDate));
    }

    @Test
    public void successPassedEventValidationInController() {
        EventDto eventValid = EventDto.builder()
                .title("Event3")
                .startDate(LocalDateTime.now().plusDays(1L))
                .build();
        eventValidator.validateEventInController(eventValid);
    }

    @Test
    void successCheckIfOwnerHasSkillsRequired() {
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        User userFirst = User.builder()
                .id(1L)
                .active(true)
                .skills(List.of(skill1, skill2))
                .build();
        Event eventFirst = Event.builder()
                .id(1L)
                .relatedSkills(List.of(skill1, skill2))
                .maxAttendees(2)
                .owner(userFirst)
                .build();
        boolean ownerHasRequiredSkills = eventFirst
                .getOwner()
                .getSkills()
                .containsAll(eventFirst.getRelatedSkills());

        assertTrue(ownerHasRequiredSkills);

    }

    @Test
    public void shouldFailCheckIfOwnerHasSkillsRequired() {
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Skill skill3 = Skill.builder().id(3L).build();

        Mockito.when(eventFirst.getOwner()).thenReturn(user);
        Mockito.when(user.getSkills()).thenReturn(List.of(skill1, skill2));
        Mockito.when(eventFirst.getRelatedSkills()).thenReturn(List.of(skill3));

        assertThrows(DataValidationException.class,
                () -> eventValidator.checkIfOwnerHasSkillsRequired(eventFirst));
    }

    @Test
    public void successCheckIfOwnerExistsById() {
        User user = User.builder()
                .id(1L)
                .active(true)
                .build();
        when(userService.checkIfOwnerExistsById(user.getId())).thenReturn(true);
        eventValidator.checkIfOwnerExistsById(user.getId());
        Mockito.verify(userService, times(1)).checkIfOwnerExistsById(user.getId());
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