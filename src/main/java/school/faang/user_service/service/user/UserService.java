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

    public User findOwnerById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Owner by id not found"));
    }
}
