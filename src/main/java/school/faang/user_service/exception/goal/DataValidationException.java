package school.faang.user_service.exception.goal;

/**
 * @author Alexander Bulgakov
 */

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}