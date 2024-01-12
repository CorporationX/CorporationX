package school.faang.user_service.dto.user;

import lombok.Data;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.UserProfilePic;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private Country country;
    private String city;
    private Integer experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> mentees;
    private List<Long> mentorIds;
    private List<Long> goalIds;
    private List<Long> skillIds;
    private UserProfilePic userProfilePic;
}