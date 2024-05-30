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
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    private static final long EVENT_ID = 1L;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
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
    public void whenFindByIdAndNotFoundThenThrowsException() {
        Assert.assertThrows(EntityNotFoundException.class,
                () -> eventService.findById(EVENT_ID));
    }

    @Test
    public void whenFindByIdThenGetEventDto() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        EventDto actual = eventService.findById(EVENT_ID);
        assertThat(actual).isEqualTo(eventDto);
    }

    @Test
    public void whenCreateEventThenGetEventDto() {
        when(eventMapper.toEntity(any())).thenReturn(event);
        when(eventMapper.toDto(any())).thenReturn(eventDto);
        when(eventRepository.save(any())).thenReturn(event);
        EventDto actual = eventService.createEvent(eventDto);
        assertThat(actual).isEqualTo(eventDto);
    }

    @Test
    public void whenFindAllThenGetListOfEventDto() {
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toDtoList(any())).thenReturn(List.of(eventDto));
        List<EventDto> actual = eventService.findAll();
        assertThat(actual).isEqualTo(List.of(eventDto));
    }
}