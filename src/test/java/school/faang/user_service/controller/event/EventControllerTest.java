package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @InjectMocks
    private EventController eventController;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventService eventService;

    @Test
    void successGetOwnedEvents() {
        long userId = 1L;
        List<Event> eventEntities = List.of(
                Event.builder()
                        .id(1L)
                        .maxAttendees(2)
                        .relatedSkills(List.of(
                                Skill.builder()
                                        .id(1L)
                                        .build()
                        ))
                        .build(),
                Event.builder()
                        .id(2L)
                        .maxAttendees(2)
                        .relatedSkills(List.of(
                                Skill.builder()
                                        .id(2L)
                                        .build()
                        ))
                        .build()
        );

        List<EventDto> eventDtos = List.of(
                EventDto.builder()
                        .id(1L)
                        .maxAttendees(2)
                        .relatedSkillIds(List.of(1L))
                        .build(),
                EventDto.builder()
                        .id(2L)
                        .maxAttendees(2)
                        .relatedSkillIds(List.of(2L))
                        .build()
        );

        Mockito.when(eventService.getOwnedEvents(userId)).thenReturn(eventDtos);
        List<EventDto> eventsExpected = eventMapper.toListDto(eventEntities);
        List<EventDto> eventsActual = eventController.getOwnedEvents(userId);

        Mockito.verify(eventService, times(1)).getOwnedEvents(userId);
        assertEquals(eventsExpected, eventsActual);
    }

    @Test
    void successGetEvent() {
        EventDto eventDtoExpected = EventDto.builder()
                .id(1L)
                .build();
        Mockito.when(eventService.getEventDto(eventDtoExpected.getId())).thenReturn(eventDtoExpected);
        EventDto eventDtoActual = eventController.getEvent(eventDtoExpected.getId());

        Mockito.verify(eventService, times(1)).getEventDto(eventDtoExpected.getId());
        assertEquals(eventDtoExpected, eventDtoActual);
    }

    @Test
    public void createEvent_shouldSuccess() {
        EventDto eventDto = EventDto.builder()
                .title("Event1")
                .startDate(LocalDateTime.now())
                .id(1L)
                .build();
        doNothing().when(eventValidator).validateEventInController(eventDto);
        eventController.create(eventDto);
        Mockito.verify(eventService, times(1)).create(eventDto);
    }

    @Test
    public void updateEvent_shouldSuccessWhenEventDtoIsValid() {
        EventDto eventDto = EventDto.builder()
                .title("Event1")
                .startDate(LocalDateTime.now())
                .id(1L)
                .build();
        doNothing().when(eventValidator).validateEventInController(eventDto);
        Mockito.when(eventService.updateEvent(eventDto)).thenReturn(eventDto);
        eventController.updateEvent(eventDto);
        Mockito.verify(eventService, times(1)).updateEvent(eventDto);
    }

    @Test
    public void updateEvent_shouldThrowsWhenEventDtoNotValid() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .build();
        doThrow(DataValidationException.class).when(eventValidator).validateEventInController(eventDto);
        assertThrows(DataValidationException.class,
                () -> eventController.updateEvent(eventDto));
    }

    @Test
    public void successGetParticipatedEventsByUserId() {
        long userId = 1L;
        List<EventDto> eventDtosExpected = List.of(
                EventDto.builder()
                        .id(1L)
                        .maxAttendees(2)
                        .build()
        );
        Mockito.when(eventService.getParticipatedEventsByUserId(userId)).thenReturn(eventDtosExpected);
        List<EventDto> eventDtosActual = eventController.getParticipatedEventsByUserId(userId);

        Mockito.verify(eventService, times(1)).getParticipatedEventsByUserId(userId);
        assertEquals(eventDtosExpected, eventDtosActual);
    }

    @Test
    public void successDeleteEvent() {
        long eventId = 1L;
        eventController.deleteEvent(eventId);
        Mockito.verify(eventService, times(1)).deleteEvent(eventId);
    }
}
