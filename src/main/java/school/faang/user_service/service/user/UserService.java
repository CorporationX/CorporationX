package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

    public boolean existsUserById(long id) {
        return userRepository.existsById(id);
    }

}
