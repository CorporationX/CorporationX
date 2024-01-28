package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.eventFilter.EventFilterValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @InjectMocks
    private EventController eventController;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventService eventService;
    @Mock
    private EventFilterValidator eventFilterValidator;

    @Test
    public void successGetEventsByFilter() {
        List<EventDto> eventDtos = new ArrayList<>(List.of(EventDto.builder()
                .id(1L)
                .build()));
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .ownerPattern(1L)
                .build();

        eventFilterValidator.checkFilterNotNull(eventFilterDto);
        Mockito.when(eventService.getEventsByFilter(eventFilterDto)).thenReturn(eventDtos);
        List<EventDto> resultEvents = eventController.getEventsByFilter(eventFilterDto);
        assertEquals(eventDtos, resultEvents);
        Mockito.verify(eventService, times(1)).getEventsByFilter(eventFilterDto);
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

}