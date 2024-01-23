package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private UserService userService;
    @Spy
    EventRepository eventRepository;
    @Mock
    private EventValidator eventValidator;

    @InjectMocks
    private EventService eventService;

    @Test
    void testSuccessCreateEventWithRequiredSkills() {
        Skill skill = Skill.builder()
                .id(1L)
                .build();
        User owner = User.builder()
                .id(1L)
                .skills(List.of(skill))
                .build();
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(owner.getId())
                .relatedSkillIds(List.of(1L))
                .maxAttendees(1)
                .build();

        Mockito.when(userService.findOwnerById(1L)).thenReturn(owner);
        Event event = eventMapper.toEntity(eventDto);

        Mockito.when(eventValidator.checkIfOwnerHasSkillsRequired(event)).thenReturn(true);
        eventService.create(eventDto);
        Mockito.verify(eventRepository, times(1)).save(event);

        Mockito.when(eventService.create(eventDto)).thenReturn(eventDto);
        assertEquals(eventDto, eventService.create(eventDto));
    }

    @Test
    public void testFailedCreateEventNotRequiredSkills() {
        Skill skill = Skill.builder()
                .id(1L)
                .build();
        User owner = User.builder()
                .id(1L)
                .skills(List.of(skill))
                .build();
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(owner.getId())
                .maxAttendees(1)
                .relatedSkillIds(List.of(2L))
                .build();

        Mockito.when(userService.findOwnerById(owner.getId())).thenReturn(owner);
        Event event = eventMapper.toEntity(eventDto);

        Mockito.when(eventValidator.checkIfOwnerHasSkillsRequired(event)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void testFailedCreateWithOwnerIsNull() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .maxAttendees(1)
                .relatedSkillIds(List.of(1L))
                .build();

        Mockito.when(userService.findOwnerById(eventDto.getOwnerId())).thenReturn(null);
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

}