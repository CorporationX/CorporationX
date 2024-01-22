package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

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
    public GoalInvitationDto acceptGoalInvitation(@PathVariable long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/reject/{id}")
    public void rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping("get/{filter}")
    public List<GoalInvitationDto> getInvitations(@PathVariable InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
