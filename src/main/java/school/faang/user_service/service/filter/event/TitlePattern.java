package school.faang.user_service.service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

public class TitlePattern extends EventFilter{
    @Override
    protected boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getTitle().contains(filter.getTitlePattern());
    }

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return !filter.getTitlePattern().isBlank();
    }
}