package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {


    UserSkillGuaranteeDto toDto(UserSkillGuarantee entity);

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "skill", source = "skillId", qualifiedByName = "mapSkill")
    @Mapping(target = "guarantor", source = "guarantorId", qualifiedByName = "mapGuarantor")
    UserSkillGuarantee toEntity(UserSkillGuaranteeDto dto);

    @Named("mapUser")
    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("mapSkill")
    default Skill mapSkill(Long skillId) {
        if (skillId == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setId(skillId);
        return skill;
    }

    @Named("mapGuarantor")
    default User mapGuarantor(Long guarantorId) {
        if (guarantorId == null) {
            return null;
        }
        User guarantor = new User();
        guarantor.setId(guarantorId);
        return guarantor;
    }
}