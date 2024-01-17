package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;


    @Test
    @DisplayName("Успешный поиск события по верному Id")
    public void testSuccessGetEventById() {
        Event eventGetById = Event.builder().id(6L).maxAttendees(5).build();
        long eventId = eventGetById.getId();

        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventGetById));

        eventService.getEvent(eventId);

        Mockito.verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    @DisplayName("Неуспешный поиск события по неверному Id")
    public void testFailedGetEventByIncorrectId() {
        long wrongId = 11L;

        Mockito.when(eventRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> eventService.getEvent(wrongId));
    }

}
