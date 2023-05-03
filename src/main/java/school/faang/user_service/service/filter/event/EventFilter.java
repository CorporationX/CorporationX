package school.faang.user_service.service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public abstract class EventFilter {

    public Stream<Event> applyFilter(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> applyFilter(event, filter));
    }

    protected abstract boolean applyFilter(Event event, EventFilterDto filter);

    public abstract boolean isApplicable(EventFilterDto filter);
}