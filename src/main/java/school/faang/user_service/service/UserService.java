package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filter.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class UserService extends AbstractUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, List<UserFilter> filters, UserMapper userMapper) {
        super(filters, userMapper);
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(UserFilterDto filter) {
        Stream<User> users = StreamSupport.stream(userRepository.findAll().spliterator(), false);
        return filterUsers(users, filter);
    }

    @Transactional(readOnly = true)
    public UserDto getUser(long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a user with id " + id)));
    }

    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean areOwnedSkills(long userId, List<Long> skillIds) {
        if (skillIds.isEmpty()) {
            return true;
        }
        return userRepository.countOwnedSkills(userId, skillIds) == skillIds.size();
    }
}
