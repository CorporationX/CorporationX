package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDTO userToUserDTO(User user);
}
