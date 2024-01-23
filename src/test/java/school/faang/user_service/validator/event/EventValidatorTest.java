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
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private EventValidator eventValidator;

    @Test
    public void validateEventInController() {
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
        EventDto eventOwnerNotValidId = EventDto.builder()
                .title("Event2")
                .startDate(LocalDateTime.now())
                .ownerId(0L)
                .build();
        EventDto eventValid = EventDto.builder()
                .title("Event3")
                .startDate(LocalDateTime.now().plusDays(1L))
                .ownerId(1L)
                .build();

        assertFalse(eventValidator.validateEventInController(eventEmptyTitle));
        assertFalse(eventValidator.validateEventInController(eventNotValidDate));
        assertFalse(eventValidator.validateEventInController(eventOwnerNotValidId));
        assertTrue(eventValidator.validateEventInController(eventValid));
    }

    @Test
    void checkIfOwnerHasSkillsRequired() {
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Skill skill3 = Skill.builder().id(3L).build();

        Skill skill4 = Skill.builder().id(4L).build();
        User user = User.builder()
                .id(1L)
                .skills(List.of(skill1, skill2, skill3))
                .build();
        Event event = Event.builder()
                .id(1L)
                .owner(user)
                .relatedSkills(List.of(skill4))
                .build();

        Mockito.when(userService.findOwnerById(event.getOwner().getId())).thenReturn(user);
        assertFalse(eventValidator.checkIfOwnerHasSkillsRequired(event));
    }

}