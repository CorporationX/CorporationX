package school.faang.user_service.controller;

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
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;

    @Test
    public void createEvent_shouldSuccess() {
        EventDto eventDto = EventDto.builder()
                .title("Event1")
                .startDate(LocalDateTime.now())
                .id(1L)
                .build();
        Mockito.when(eventValidator.validateEventInController(eventDto)).thenReturn(true);
        eventController.create(eventDto);
        Mockito.verify(eventService, times(1)).create(eventDto);
    }

    @Test
    public void createEvent_shouldFailed() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .build();
        Mockito.when(eventValidator.validateEventInController(eventDto)).thenReturn(false);
        eventController.create(eventDto);
        Mockito.verify(eventService, Mockito.never()).create(eventDto);
    }

}
