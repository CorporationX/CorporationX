package faang.school.urlshortenerservice.exception;

import java.net.URISyntaxException;

public class InternalUrlServiceException extends RuntimeException {
    public InternalUrlServiceException(String message, URISyntaxException e) {
        super(message);
    }
}