package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDto;

public interface UserService {
    boolean existsById(long skillId);
    UserDto createUser(UserDto userDto);
}