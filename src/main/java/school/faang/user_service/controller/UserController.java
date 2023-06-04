package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserProfileDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    public ResponseEntity<String> uploadProfilePic(@PathVariable Long userId, @RequestPart(value = "file") MultipartFile file) {
        userService.uploadProfilePic(userId, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/uploadUnmappedUserList")
    public ResponseEntity<String> uploadFiles(@RequestPart(value = "csvFile") MultipartFile csvFile) {
        try {
            InputStream csvInputStream = csvFile.getInputStream();
            userService.processUnmappedUserData(csvInputStream);
            return ResponseEntity.ok("Data converted successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File reading exception");
        }
    }

    @GetMapping(value = "/{userId}/profilePicSmall", produces = {"image/jpg", "image/jpeg", "image/png"})
    public byte[] getProfilePicSmall(@PathVariable Long userId) {
        return userService.getProfilePic(userId, true);
    }

    @GetMapping(value = "/{userId}/profilePic", produces = {"image/jpg", "image/jpeg", "image/png"})
    public byte[] getProfilePic(@PathVariable Long userId) {
        return userService.getProfilePic(userId, false);
    }

    @DeleteMapping("/{userId}/profilePic")
    public ResponseEntity<String> deleteProfilePic(@PathVariable Long userId) {
        userService.deleteProfilePic(userId);
        return ResponseEntity.ok().build();
    }
}