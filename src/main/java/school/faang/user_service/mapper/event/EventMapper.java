package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.skill.SkillMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = SkillMapper.class)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
//    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "toSkillDto")
    EventDto toDto(Event event);

    //, qualifiedByName = "toUser")
    @Mapping(source = "ownerId", target = "owner", ignore = true)
    Event toEntity(EventDto eventDto);

//    @Named("toUser")
//    default User toUser(Long id) {
//        return User.builder()
//                .id(id)
//                .active(true)
//                .build();
//    }

}
