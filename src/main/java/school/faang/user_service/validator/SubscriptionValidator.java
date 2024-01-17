package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepository;

    public void validate(long followerId, long followeeId) {
        if (!userRepository.existsById(followerId) || !userRepository.existsById(followeeId)) {
            throw new DataValidationException("This user is not registered");
        }
        if (subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Subscribe already exists!");
        }
        if (followerId == followeeId) {
            throw new DataValidationException("You can't subscribe to yourself!");
        }
    }

//    public void validateUserExists(long userId) {
//        if (userRepository.existsById(followerId) || userRepository.existsById(followeeId)) {
//            throw new DataValidationException("This user is not registered");
//        }
//    }
}
