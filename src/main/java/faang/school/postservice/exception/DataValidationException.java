package faang.school.postservice.exception;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message) {
        super(message);
    }
}
