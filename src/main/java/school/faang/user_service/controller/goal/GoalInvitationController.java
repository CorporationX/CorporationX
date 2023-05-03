package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/goal/invitation")
    public GoalInvitationDto createGoalInvitation(@RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/goal/invitation/{id}/accept")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable Long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/goal/invitation/{id}/reject")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable Long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @PostMapping("/goal/invitation/list")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}