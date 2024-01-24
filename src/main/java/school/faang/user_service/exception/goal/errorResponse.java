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
public class errorResponse {
    private int statusCode;
    private String message;

    public errorResponse() {
    }

    public errorResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public errorResponse(int statusCode, String message) {
        this(statusCode);
        this.message = message;
    }
}
