package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.filter.event.EventFilter;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class AbstractEventService {

    private final List<EventFilter> filters;
    private final EventMapper eventMapper;

    protected List<EventDto> filterEvents(Stream<Event> events, EventFilterDto filter) {
        filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .forEach(eventFilter -> eventFilter.applyFilter(events, filter));
        return events
                .skip((long) filter.getPageSize() * filter.getPage())
                .limit(filter.getPageSize())
                .map(eventMapper::toDto)
                .toList();
    }
}