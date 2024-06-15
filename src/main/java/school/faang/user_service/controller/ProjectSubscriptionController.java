package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.project_subscription.ProjectSubscriptionService;

@RestController
@RequestMapping("/api/v1/project")
@AllArgsConstructor
public class ProjectSubscriptionController {
    private final ProjectSubscriptionService projectSubscriptionService;

    @PostMapping("/subscribe")
    public void followUser(@RequestParam(name = "followerId") long followerId,
                           @RequestParam(name = "projectId") long projectId) {
        projectSubscriptionService.followProject(followerId, projectId);
    }
}