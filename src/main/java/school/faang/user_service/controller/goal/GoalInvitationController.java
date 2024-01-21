package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

/**
 * @author Alexander Bulgakov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/goal-invitation")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/accept/{id}")
    public void acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }
}
