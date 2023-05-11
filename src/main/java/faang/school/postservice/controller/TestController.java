package faang.school.postservice.controller;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @PostMapping("/test")
    public void test() {
        userServiceClient.getUser(userContext.getUserId());
    }
}
