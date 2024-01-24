package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private Event event;

    @InjectMocks
    private EventValidator eventValidator;

    @Test
    public void successCheckEventIsExistWhenExists() {
        Mockito.when(eventRepository.existsById(event.getId())).thenReturn(true);
        assertTrue(eventValidator.checkEventIsExistById(event.getId()));
    }

    @Test
    public void shouldFailedCheckEventIsExistWhenNotExists() {
        Mockito.when(eventRepository.existsById(event.getId())).thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> eventValidator.checkEventIsExistById(event.getId()));
    }

}
