package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.messaging.UserProfileViewEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserProfileDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.messaging.UserProfileViewPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filter.user.UserFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class UserService extends AbstractUserService {

    private final UserRepository userRepository;
    private final UserContext userContext;
    private final UserProfileViewPublisher userProfileViewPublisher;

    public UserService(UserRepository userRepository, List<UserFilter> filters,
                       UserMapper userMapper, UserContext userContext,
                       UserProfileViewPublisher userProfileViewPublisher) {
        super(filters, userMapper);
        this.userRepository = userRepository;
        this.userContext = userContext;
        this.userProfileViewPublisher = userProfileViewPublisher;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(UserFilterDto filter) {
        Stream<User> users = StreamSupport.stream(userRepository.findAll().spliterator(), false);
        return filterUsers(users, filter);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return StreamSupport.stream(userRepository.findAllById(ids).spliterator(), false)
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUser(long id) {
        return userMapper.toDto(findUser(id));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(long id) {
        UserProfileDto profile = userMapper.toProfileDto(findUser(id));
        if (userContext.getUserId() != id) {
            userProfileViewPublisher.publish(id, userContext.getUserId());
        }
        return profile;
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

    private User findUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a user with id " + id));
    }
}
