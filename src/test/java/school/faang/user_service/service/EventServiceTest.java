package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private EventValidator eventValidator;
    private EventDto eventDto;
    private Event event;
    private User owner;

    @BeforeEach
    void init() {
        eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .build();
        owner = User.builder()
                .id(1L)
                .active(true)
                .build();
        event = eventMapper.toEntity(eventDto);
        event.setOwner(owner);
    }

    @Test
    public void successUpdateEventWhenAllIsValid() {
        Mockito.when(eventValidator.checkEventIsExistById(eventDto.getId())).thenReturn(true);
        Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(event);
        Mockito.when(eventValidator.checkIfOwnerExistsById(event.getOwner().getId())).thenReturn(true);
        Mockito.when(eventValidator.checkIfOwnerHasSkillsRequired(event)).thenReturn(true);

        eventService.updateEvent(eventDto);
        Mockito.verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void shouldFailedUpdateEventWhenEventByIdNotFound() {
        long id = 1L;
        Mockito.when(eventValidator.checkEventIsExistById(id)).thenReturn(false);

        eventService.updateEvent(eventDto);
        Mockito.verify(eventRepository, never()).save(event);
    }

    @Test
    public void shouldFailedUpdateEventWhenOwnerByIdNotFound() {
        Mockito.when(eventValidator.checkEventIsExistById(eventDto.getId())).thenReturn(true);
        Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(event);
        Mockito.when(eventValidator.checkIfOwnerExistsById(event.getOwner().getId())).thenReturn(false);

        eventService.updateEvent(eventDto);
        Mockito.verify(eventRepository, never()).save(event);
    }

    @Test
    public void shouldFailedUpdateEventWhenOwnerNotExistRequiredSkills() {
        Mockito.when(eventValidator.checkEventIsExistById(eventDto.getId())).thenReturn(true);
        Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(event);
        Mockito.when(eventValidator.checkIfOwnerExistsById(event.getOwner().getId())).thenReturn(true);
        Mockito.when(eventValidator.checkIfOwnerHasSkillsRequired(event)).thenReturn(false);

        eventService.updateEvent(eventDto);
        Mockito.verify(eventRepository, never()).save(event);
    }

}
