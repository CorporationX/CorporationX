package faang.school.urlshortenerservice.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUrlException extends ValidationException {
    public InvalidUrlException(String message) {
        super(message);
    }
}