package school.faang.user_service.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserProfileDto;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/list")
    public List<UserDto> getUsers(@RequestBody @Validated UserFilterDto filter) {
        return userService.getUsers(filter);
    }

    @PostMapping
    public List<UserDto> getUsers(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/user/{userId}/profile")
    public UserProfileDto getUserProfile(@PathVariable long userId) {
        return userService.getUserProfile(userId);
    }

    @PostMapping("/{userId}/profilePic")
    public ResponseEntity<String>  uploadProfilePic(@PathVariable Long userId, @RequestPart(value = "file") MultipartFile file) {
        userService.uploadProfilePic(userId, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{userId}/profilePicSmall", produces = {"image/jpg, image/jpeg, image/png"})
    public byte[] getProfilePicSmall(@PathVariable Long userId) {
        return userService.getProfilePic(userId, true);
    }

    @GetMapping(value = "/{userId}/profilePic", produces = {"image/jpg, image/jpeg, image/png"})
    public byte[] getProfilePic(@PathVariable Long userId) {
        return userService.getProfilePic(userId, false);
    }

    @DeleteMapping("/{userId}/profilePic")
    public ResponseEntity<String> deleteProfilePic(@PathVariable Long userId) {
        userService.deleteProfilePic(userId);
        return ResponseEntity.ok().build();
    }
}
