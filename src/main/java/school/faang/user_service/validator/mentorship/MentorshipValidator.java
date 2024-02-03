package school.faang.user_service.validator.mentorship;

public class MentorshipValidator {
    public static void validateMentorshipIds(long mentorId, long menteeId) {
        if (mentorId == menteeId) {
            throw new IllegalArgumentException("MentorId and MenteeId cannot be the same");
        }
    }
}