package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PutMapping("/accept/{id}")
    public ResponseEntity<GoalInvitationDto> acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<GoalInvitationDto> rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
        return ResponseEntity.ok().build();
    }
}
