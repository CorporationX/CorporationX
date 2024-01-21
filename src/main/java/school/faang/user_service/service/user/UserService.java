package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @SneakyThrows
    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + " is not exists"));
    }

    public boolean existsUserById(long id) {
        return userRepository.existsById(id);
    }

}
