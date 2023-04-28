package school.faang.user_service.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(ErrorMessage errorMessage, Object... arguments) {
        super(String.format(errorMessage.getMessage(), arguments));
    }
}