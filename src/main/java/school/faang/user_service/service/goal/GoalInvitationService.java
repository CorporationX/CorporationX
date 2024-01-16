package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;

    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow();

        List<GoalInvitation> receivedGoalInvitations = new ArrayList<>();
        receivedGoalInvitations.add(goalInvitation);

        List<Goal> goals = new ArrayList<>();
        goals.add(goalInvitation.getGoal());

        User invitedUser = goalInvitation.getInvited();

        if (checkData(invitedUser, goalInvitation)) {
            invitedUser.setReceivedGoalInvitations(receivedGoalInvitations);
            invitedUser.setGoals(goals);
        }
    }

    public void rejectGoalInvitation(long id) {

    }

    private boolean checkData(User user, GoalInvitation goalInvitation) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User is not found");
        } else if (!goalInvitationRepository.existsById(goalInvitation.getId())) {
            throw new IllegalArgumentException("GoalInvitation is not found");
        } else if (user.getGoals().contains(goalInvitation.getGoal())) {
            throw new IllegalArgumentException("User already exist this goal");
        } else if (!(user.getGoals().size() < MAX_ACTIVE_GOALS)) {
            throw new IllegalArgumentException("User already have maximum active goals");
        } else {
            return true;
        }
    }


}
