package school.faang.user_service.dto.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}