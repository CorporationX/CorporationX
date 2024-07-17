package school.faang.user_service.service.project_subscription;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.FollowerEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.validator.ProjectSubscriptionValidator;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProjectSubscriptionServiceImpl implements ProjectSubscriptionService {
    private final ProjectSubscriptionRepository projectSubscriptionRepository;
    private final FollowerEventPublisher followerEventPublisher;
    private final ProjectSubscriptionValidator projectSubscriptionValidator;

    @Override
    public void followProject(long followerId, long projectId) {
        if (existsByFollowerIdAndProjectId(followerId, projectId)) {
            throw new DataValidationException(String.format("пользователь с id= %d уже подписан на проект с id= %d", followerId, projectId));
        }
        projectSubscriptionValidator.validateProjectSubscription(followerId, projectId);
        projectSubscriptionRepository.followProject(followerId, projectId);
        FollowerEvent followerEvent = FollowerEvent.builder()
                .followerId(followerId)
                .projectId(projectId)
                .followingDate(LocalDateTime.now().withNano(0))
                .build();
        followerEventPublisher.publish(followerEvent);
    }

    private boolean existsByFollowerIdAndProjectId(long followerId, long projectId) {
        return projectSubscriptionRepository.existsByFollowerIdAndProjectId(followerId, projectId);
    }
}