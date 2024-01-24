package school.faang.user_service.exception.goal;

/**
 * @author Alexander Bulgakov
 */

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
