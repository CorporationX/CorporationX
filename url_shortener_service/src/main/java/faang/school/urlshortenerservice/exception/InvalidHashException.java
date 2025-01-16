package faang.school.urlshortenerservice.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.BAD_REQUEST)
public class InvalidHashException extends ValidationException {
    public InvalidHashException(String message) {
        super(message);
    }
}