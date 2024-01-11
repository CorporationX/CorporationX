package school.faang.user_service.execption;

public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }
}
