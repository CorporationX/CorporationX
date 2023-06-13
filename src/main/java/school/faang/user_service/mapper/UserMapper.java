package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserProfileDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserDto userDto);

    @Mapping(target = "preference", source = "contactPreference.preference")
    UserDto toDto(User user);

    @Mapping(target = "premium", expression = "java(premium != null ? true : false)")
    UserProfileDto toProfileDto(User user);
}