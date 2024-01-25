package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId", ignore = true)
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner", ignore = true)
    Event toEntity(EventDto eventDto);

    default List<EventDto> toEventDto(List<Event> events) {
        return events.stream()
                .map(this::toDto).toList();
    }

}
