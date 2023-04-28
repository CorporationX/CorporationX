package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    EventDto toDto(Event entity);

    Event toEntity(EventDto dto);
}