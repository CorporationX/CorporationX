package school.faang.user_service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;

@RestController
@RequiredArgsConstructor
public class ControllerTest {
    private final UserContext userContext;

    @GetMapping("testId")
    public long getUserIdPlusTen(){
        return userContext.getUserId() + 10;
    }
}
