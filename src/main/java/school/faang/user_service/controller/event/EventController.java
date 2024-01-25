package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.eventFilter.EventFilterValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventFilterValidator eventFilterValidator;

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        eventFilterValidator.checkFilterNotNull(filterDto);

        return eventService.getEventsByFilter(filterDto);
    }

}
