package school.faang.user_service.dto.user;

import lombok.Data;

@Data
public class UserProfileDto {
    private long id;
    private String username;
    private String email;
    private String aboutMe;
    private String city;
    private Integer experience;
}
