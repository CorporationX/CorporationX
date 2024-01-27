package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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

}