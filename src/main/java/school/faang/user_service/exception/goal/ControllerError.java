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
public class ControllerError {
    private int statusCode;
    private String message;

    public ControllerError() {
    }

    public ControllerError(int statusCode) {
        this.statusCode = statusCode;
    }

    public ControllerError(int statusCode, String message) {
        this(statusCode);
        this.message = message;
    }
}
