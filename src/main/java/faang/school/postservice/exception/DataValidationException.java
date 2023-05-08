package faang.school.postservice.exception;

public class DataValidationException extends BusinessException {
    public DataValidationException(String message) {
        super("Bad request", message);
    }
}
