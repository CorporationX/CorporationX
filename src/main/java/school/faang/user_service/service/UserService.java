package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filter.UserFilter;

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
}
