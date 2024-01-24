package school.faang.user_service.exception.goal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Alexander Bulgakov
 */

@Getter
@Setter
@ToString
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String url;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public ErrorResponse(String url, int statusCode, String message) {
        this(statusCode);
        this.url = url;
        this.message = message;
    }
}
