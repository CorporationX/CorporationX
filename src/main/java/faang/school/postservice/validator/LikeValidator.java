package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final UserServiceClient userServiceClient;

    public void validate(LikeDto like) {
        if (like.getUserId() == null) {
            throw new DataValidationException("Author id is required");
        }
        if (userServiceClient.getUser(like.getUserId()) == null) {
            throw new DataValidationException("Author with id " + like.getUserId() + " does not exist");
        }
    }
}
