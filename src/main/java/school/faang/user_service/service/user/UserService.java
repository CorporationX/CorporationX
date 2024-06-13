package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDTO;

import java.util.List;

public interface UserService {
    boolean existsById(long skillId);
    UserDTO createUser(UserDTO userDto);
    void deactivateUser(long userId);
    UserDTO findById(long userId);
    List<UserDTO> findAll();
    UserDTO update(UserDTO userDto);
}