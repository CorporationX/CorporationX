package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import static org.mockito.Mockito.never;

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

    private EventDto eventDto;
    private Event event;
    private User owner;

    @BeforeEach
    public void init() {
        owner = User.builder()
                .id(1L)
                .active(true)
                .build();
        eventDto = EventDto.builder()
                .title("Event1")
                .id(1L)
                .ownerId(1L)
                .maxAttendees(2)
                .build();
        event = Event.builder()
                .id(1L)
                .owner(owner)
                .maxAttendees(2)
                .build();
    }

    @Test
    public void shouldCreateAndSaveEventWhenTwoPassedValidation() {
        Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(event);
        Mockito.when(eventValidator.checkIfOwnerExistsById(event.getOwner().getId())).thenReturn(true);
        Mockito.when(eventValidator.checkIfOwnerHasSkillsRequired(event)).thenReturn(true);

        eventService.create(eventDto);

        Mockito.verify(eventRepository).save(event);
    }

    @Test
    public void shouldNotSaveEventWhenOwnerNotExistsById() {
        Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(event);
        Mockito.when(eventValidator.checkIfOwnerExistsById(event.getOwner().getId())).thenReturn(false);

        eventService.create(eventDto);

        Mockito.verify(eventRepository, never()).save(event);
    }

    @Test
    public void shouldNotSaveEventWhenOwnerDoesNotHaveRequiredSkills() {
        Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(event);
        Mockito.when(eventValidator.checkIfOwnerExistsById(event.getOwner().getId())).thenReturn(true);
        Mockito.when(eventValidator.checkIfOwnerHasSkillsRequired(event)).thenReturn(false);

        eventService.create(eventDto);

        Mockito.verify(eventRepository, never()).save(event);
    }

}