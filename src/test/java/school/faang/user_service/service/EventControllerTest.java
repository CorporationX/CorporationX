package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @InjectMocks
    private EventController eventController;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventService eventService;

    @Test
    public void updateEvent_shouldSuccess() {
        EventDto eventDto = EventDto.builder()
                .title("Event1")
                .startDate(LocalDateTime.now())
                .id(1L)
                .build();
        Mockito.when(eventValidator.validateEventInController(eventDto)).thenReturn(true);
        eventController.updateEvent(eventDto);
        Mockito.verify(eventService, times(1)).updateEvent(eventDto);
    }

    @Test
    public void updateEvent_shouldFailed() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .build();
        Mockito.when(eventValidator.validateEventInController(eventDto)).thenReturn(false);
        eventController.updateEvent(eventDto);
        Mockito.verify(eventService, Mockito.never()).updateEvent(eventDto);
    }

}
