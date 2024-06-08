package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    private static final long EVENT_ID = 1L;

    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventServiceImpl eventService;
    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        event = new Event();
        eventDto = new EventDto();
        event.setId(EVENT_ID);
        eventDto.setId(EVENT_ID);
    }

    @Test
    public void whenDeleteByIdAndIdNotExistThenThrowException() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(false);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> eventService.deleteById(EVENT_ID));
    }

    @Test
    public void whenDeleteByIdSuccessfully() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        eventService.deleteById(EVENT_ID);
        verify(eventRepository).deleteById(EVENT_ID);
    }
}