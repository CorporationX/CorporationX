package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<User> toListUser(List<UserDto> dtos);

    List<UserDto> toListUserDto(List<User> users);
}
