package school.faang.user_service.mapper.event;

import org.mapstruct.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillIds", qualifiedByName = "toSkillIds")
    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    @Named("toSkillIds")
    default List<Long> toSkillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }


}