package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {

    @Spy
    private EventMapperImpl eventMapper;

    private Event event;
    private EventDto eventDto;

    @BeforeEach
    public void init() {
        Event event = Event.builder()
                .id(1L)
                .build();
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .maxAttendees(0)
                .build();
    }

    @Test
    void toDtoAndToEntity() {
        assertEquals(eventDto, eventMapper.toDto(event));
        assertEquals(event, eventMapper.toEntity(eventDto));
    }

}