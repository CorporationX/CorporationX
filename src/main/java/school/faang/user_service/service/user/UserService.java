package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDTO;

public interface UserService {
    boolean existsById(long skillId);
    UserDTO createUser(UserDTO userDto);
}