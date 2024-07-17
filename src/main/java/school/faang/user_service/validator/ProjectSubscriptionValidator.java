package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.user.UserService;

import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class ProjectSubscriptionValidator {
    private final UserService userService;
    private final ProjectSubscriptionRepository projectSubscriptionRepository;

    public void validateProjectSubscription(long userId, long projectId) {
        userService.existsById(userId);
        checkProjectExists(projectId);
    }

    private void checkProjectExists(long projectId) {
        if (!projectSubscriptionRepository.existsById(projectId)) {
            throw new NoSuchElementException(String.format("project with id: %d is not exists", projectId));
        }
    }
}