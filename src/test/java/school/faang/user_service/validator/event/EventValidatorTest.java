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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private EventValidator eventValidator;


    @Test
    void successValidateEventToUpdate() {
        User owner = User.builder()
                .id(1L)
                .active(true)
                .skills(List.of(Skill.builder()
                                .id(1L)
                                .build(),
                        Skill.builder()
                                .id(2L)
                                .build()))
                .build();
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .startDate(LocalDateTime.now().plusDays(1L))
                .ownerId(1L)
                .relatedSkillIds(List.of(1L, 2L))
                .build();

        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(owner));
        eventValidator.validateEventToUpdate(eventDto);
    }

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


    @Test
    void shouldFailedValidateEventInControllerWhenTitleIsBlank() {
        EventDto eventDtoTitleNull = EventDto.builder()
                .title("     ")
                .build();
        assertThrows(DataValidationException.class,
                () -> eventValidator.validateEventInController(eventDtoTitleNull));
    }

    @Test
    void shouldFailedValidateEventInControllerWhenDateNotValid() {
        EventDto eventDtoDateNotValid = EventDto.builder()
                .title("EventFirst")
                .startDate(LocalDateTime.now().minusDays(1L))
                .build();
        assertThrows(DataValidationException.class,
                () -> eventValidator.validateEventInController(eventDtoDateNotValid));
    }

    @Test
    void successCheckIfOwnerHasSkillsRequiredWhenOwnerSkillsExists() {
        Skill skillFirst = Skill.builder()
                .id(1L)
                .build();
        Skill skillSecond = Skill.builder()
                .id(2L)
                .build();
        List<Skill> skills = List.of(skillFirst, skillSecond);
        User owner = User.builder()
                .id(1L)
                .active(true)
                .skills(skills)
                .build();
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .relatedSkillIds(List.of(skillFirst.getId(), skillSecond.getId()))
                .build();
        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.ofNullable(owner));
        eventValidator.checkIfOwnerHasSkillsRequired(eventDto);
    }

    @Test
    void shouldFailedCheckIfOwnerHasSkillsRequiredWhenOwnerSkillsNotExists() {
        Skill skillFirst = Skill.builder()
                .id(1L)
                .build();
        Skill skillSecond = Skill.builder()
                .id(2L)
                .build();
        List<Skill> skills = List.of(skillFirst, skillSecond);
        User owner = User.builder()
                .id(1L)
                .active(true)
                .skills(skills)
                .build();
        List<Long> wrongSkillIds = List.of(3L, 4L);
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .relatedSkillIds(wrongSkillIds)
                .build();
        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.ofNullable(owner));
        assertThrows(DataValidationException.class,
                () -> eventValidator.checkIfOwnerHasSkillsRequired(eventDto));
    }

    @Test
    void shouldThrowCheckIfOwnerHasSkillsRequiredWhenOwnerNotFound() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .build();
        assertThrows(DataValidationException.class,
                () -> eventValidator.checkIfOwnerHasSkillsRequired(eventDto));
    }

}