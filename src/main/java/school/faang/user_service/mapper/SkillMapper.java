package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto toDto(Skill entity);

    Skill toEntity(SkillDto dto);
}
