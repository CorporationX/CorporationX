package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @InjectMocks
    private EventController eventController;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventService eventService;

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

}
