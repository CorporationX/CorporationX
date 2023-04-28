package school.faang.user_service.exception;

public enum ErrorMessage {

    SKILL_ALREADY_EXISTS("A skill with the title %s already exists"),
    INVALID_SKILL_PROVIDED("Invalid skill is provided for creation"),
    EVENT_NOT_FOUND("Event not found with id: %s"),
    NOT_ENOUGH_SKILL_OFFERS("At least %s skill offers are required to acquire a skill"),
    INVALID_RECOMMENDATION("Invalid recommendation data is provided"),
    INVALID_EVENT("Invalid event data is provided"),
    INVALID_USER_SKILLS("User does not have skills provided in the event"),
    USER_ALREADY_REGISTERED("User has already been registered for this event"),
    USER_NOT_REGISTERED("User is not registered for this event"),
    INAPPLICABLE_USER("Only the event owner can add skills"),
    ;
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}