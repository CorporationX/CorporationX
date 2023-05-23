package school.faang.user_service.dto.skill;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.UserSkillGuaranteeDto;

import java.util.List;

@Data
@NoArgsConstructor
public class SkillDto {
    private Long id;
    private String title;
    private List<UserSkillGuaranteeDto> guarantees;
}