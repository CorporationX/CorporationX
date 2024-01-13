package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface SkillMapper {

    @Mapping(source = "users", target = "userIds", qualifiedByName = "map")
    SkillDto toDto(Skill skill);

    @Mapping(target = "owned.id", ignore = true)
    Skill toEntity(SkillDto skillDto);

    @Named(value = "map")
    default List<Long> map(List<User> users) {
        return users == null ? new ArrayList<>() : users.stream().map(User::getId).toList();
    }

    @Named(value = "toSkillDto")
    default List<SkillDto> toSkillDto(List<Skill> skills) {
        return skills.stream()
                .map(this::toDto).collect(Collectors.toList());
    }
}
