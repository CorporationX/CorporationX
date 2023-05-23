package school.faang.user_service.exception;

public enum ErrorMessage {

    SKILL_ALREADY_EXISTS("A skill with the title %s already exists"),
    INVALID_SKILL_PROVIDED("Invalid skill is provided for creation"),
    EVENT_NOT_FOUND("Event not found with id: %s"),
    USER_NOT_FOUND("User not found with id: %s"),
    NOT_ENOUGH_SKILL_OFFERS("At least %s skill offers are required to acquire a skill"),
    RECOMMENDATION_REQUEST_ALREADY_EXISTS("Recommendation request from requester with id %d to receiver with id %d already exists. " +
            "The next one can be sent %s months after the previous one"),
    RECOMMENDATION_REQUEST_NOT_FOUND("There's no recommendation request with id %d"),
    INVALID_MENTORSHIP_REQUEST("Invalid mentorship request"),
    INVALID_RECOMMENDATION("Invalid recommendation data is provided"),
    INVALID_EVENT("Invalid event data is provided"),
    INVALID_USER_SKILLS("User does not have skills provided in the event"),
    USER_ALREADY_REGISTERED("User has already been registered for this event"),
    USER_NOT_REGISTERED("User is not registered for this event"),
    INAPPLICABLE_USER("Only the event owner can add skills"),
    FILE_EXCEPTION("Error with reading file %s"),
    INVALID_GOAL_PROVIDED("Invalid goal is provided for creation"),
    GOAL_NOT_FOUND("There's no goal with id: %s"),
    TOO_MANY_GOALS("User set too many goals at a time. Max active goals possible is %s"),
    GOAL_ALREADY_COMPLETED("The goal is already completed"),
    ;
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}