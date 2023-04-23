package school.faang.user_service.exception;

public enum ErrorMessage {

    SKILL_ALREADY_EXISTS("A skill with the title %s already exists"),
    INVALID_SKILL_PROVIDED("Invalid skill is provided for creation"),
    NOT_ENOUGH_SKILL_OFFERS("At least %s skill offers are required to acquire a skill"),
    ;
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
