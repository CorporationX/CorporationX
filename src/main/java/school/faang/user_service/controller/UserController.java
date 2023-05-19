package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserProfileDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/list")
    public List<UserDto> getUsers(@RequestBody UserFilterDto filter) {
        return userService.getUsers(filter);
    }

    @PostMapping("/users")
    public List<UserDto> getUsers(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/user/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/user/{userId}/profile")
    public UserProfileDto getUserProfile(@PathVariable long userId) {
        return userService.getUserProfile(userId);
    }
}
