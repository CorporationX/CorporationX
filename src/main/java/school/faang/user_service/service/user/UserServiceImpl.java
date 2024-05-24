package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.profile_picture.ProfilePictureService;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfilePictureService profilePictureService;
    private final UserMapper userMapper;

    @Override
    public boolean existsById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(String.format("user with id: %d is not exists", userId));
        }
        return true;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        profilePictureService.assignPictureToUser(user);
        return userMapper.toDto(userRepository.save(user));
    }
}