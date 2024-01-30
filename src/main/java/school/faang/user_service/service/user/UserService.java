package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void checkIfOwnerExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("Owner does not exist");
        }
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new DataValidationException(String.format("User with ID %d not found", id)));
    }

    public boolean checkIfOwnerExistsById(Long id) {
        return userRepository.existsById(id);
    }

}
