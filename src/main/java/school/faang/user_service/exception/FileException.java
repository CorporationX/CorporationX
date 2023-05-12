package school.faang.user_service.exception;

public class FileException extends RuntimeException {

    public FileException(ErrorMessage errorMessage, Object... arguments) {
        super(String.format(errorMessage.getMessage(), arguments));
    }

    public FileException(String message) {
        super(message);
    }
}
