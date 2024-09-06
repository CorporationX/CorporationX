package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  @Mapping(source = "username", target = "name")
  UserDto toDto(User user);
  @Mapping(source = "name", target = "username")
  User toEntity(UserDto userDto);
}
