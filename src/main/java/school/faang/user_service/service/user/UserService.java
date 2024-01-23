package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean checkIfOwnerExistsById(Long id) {
        return userRepository.existsById(id);
    }
}
