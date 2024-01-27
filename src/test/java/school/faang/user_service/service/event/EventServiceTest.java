package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventMapper eventMapper;
    @Mock
    EventRepository eventRepository;
    @Mock
    private EventValidator eventValidator;
    @InjectMocks
    private EventService eventService;

    @Test
    public void successCreateAndSaveEventWhenTwoPassedValidation() {
        Skill skillFirst = Skill.builder()
                .id(1L)
                .build();
        Skill skillSecond = Skill.builder()
                .id(1L)
                .build();
        User owner = User.builder()
                .id(1L)
                .active(true)
                .skills(List.of(skillFirst, skillSecond))
                .build();
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("EventFirst")
                .ownerId(owner.getId())
                .relatedSkillIds(List.of(skillFirst.getId(), skillSecond.getId()))
                .build();
        Event eventEntity = Event.builder()
                .id(1L)
                .title("EventFirst")
                .owner(owner)
                .relatedSkills(List.of(skillFirst, skillSecond))
                .maxAttendees(2)
                .build();

        when(eventMapper.toEntity(eventDto)).thenReturn(eventEntity);
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        when(eventMapper.toDto(eventEntity)).thenReturn(eventDto);
        doNothing().when(eventValidator).checkIfOwnerExistsById(owner.getId());
        doNothing().when(eventValidator).checkIfOwnerHasSkillsRequired(eventEntity);
        EventDto actual = eventService.create(eventDto);

        verify(eventValidator).checkIfOwnerExistsById(owner.getId());
        verify(eventValidator).checkIfOwnerHasSkillsRequired(eventEntity);
        verify(eventRepository).save(eventEntity);
        assertEquals(eventDto, actual);
    }

    @Test
    public void shouldNotSaveEventWhenOwnerNotExistsById() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .title("EventFirst")
                .build();
        User owner = User.builder()
                .id(22L)
                .active(true)
                .build();
        Event eventEntity = Event.builder()
                .id(1L)
                .owner(owner)
                .maxAttendees(2)
                .build();
        when(eventMapper.toEntity(any(EventDto.class))).thenReturn(eventEntity);
        doThrow(new DataValidationException("Owner does not exist"))
                .when(eventValidator).checkIfOwnerExistsById(anyLong());

        assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto));
    }

    @Test
    public void shouldNotSaveEventWhenOwnerDoesNotHaveRequiredSkills() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .title("EventFirst")
                .build();
        User owner = User.builder()
                .id(22L)
                .active(true)
                .build();
        Event eventEntity = Event.builder()
                .id(1L)
                .owner(owner)
                .maxAttendees(2)
                .build();
        when(eventMapper.toEntity(eventDto)).thenReturn(eventEntity);
        doNothing().when(eventValidator).checkIfOwnerExistsById(owner.getId());
        doThrow(new DataValidationException("Owner does not have required skills"))
                .when(eventValidator).checkIfOwnerHasSkillsRequired(eventEntity);

        assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto));
    }

}