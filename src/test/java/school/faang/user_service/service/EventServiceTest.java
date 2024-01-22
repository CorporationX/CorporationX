package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;

    @Test
    @DisplayName("Успешное получение всех событий по верному Id пользователя")
    public void testGetParticipatedEventsByUserId() {
        long userId = 1L;

        List<Event> mockEvents = List.of(
                Event.builder()
                        .id(21L)
                        .title("EventOne")
                        .maxAttendees(2)
                        .build(),
                Event.builder()
                        .id(22L)
                        .title("EventTwo")
                        .maxAttendees(2)
                        .build()
        );

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(mockEvents);
        List<Event> events = eventService.getParticipatedEventsByUserId(userId);

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
        assertEquals(mockEvents, events);
    }

    @Test
    @DisplayName("Неуспешное получение событий по не верному Id пользователя")
    public void testFailedGetEventByIncorrectId() {
        User user1 = User.builder().id(-5L).active(true).build();
        long userId = user1.getId();

        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        List<Event> events = eventService.getParticipatedEventsByUserId(userId);

        verify(eventRepository, Mockito.never()).findAllByUserId(userId);
        assertEquals(Collections.emptyList(), events);
    }

}
