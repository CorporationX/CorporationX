package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private EventValidator eventValidator;

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
