package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@RequiredArgsConstructor
@Service
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    public void deleteMentor(long menteeId, long mentorId) {
        User mentorToDelete = validateAndGet(mentorId);
        User menteeOfMentor = validateAndGet(menteeId);
        //delete mentor if mentor contains mentee
        menteeOfMentor.getMentors().removeIf(mentor -> mentor.equals(mentorToDelete)
                && mentorToDelete.getMentees().contains(menteeOfMentor));
    }

    private User validateAndGet(long id) {
        return mentorshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found"));
    }
}