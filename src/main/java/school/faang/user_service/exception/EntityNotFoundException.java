package school.faang.user_service.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(ErrorMessage errorMessage, Object... arguments) {
        super(String.format(errorMessage.getMessage(), arguments));
    }
}
