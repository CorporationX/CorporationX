package school.faang.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSkillGuaranteeDto {
    private Long id;
    private Long userId;
    private Long skillId;
    private Long guarantorId;
}