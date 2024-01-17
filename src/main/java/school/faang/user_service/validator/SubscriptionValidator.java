package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final UserRepository userRepo;

    public void validate(long userId) {
        if (!userRepo.existsById(userId)) {
            throw new DataValidationException("This user is not registered");
        }
    }
}