package faang.school.postservice.exception;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super("Resource not found", message);
    }
}
