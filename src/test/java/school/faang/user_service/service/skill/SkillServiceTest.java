package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;

    @Test
    void successGetRelatedSkills() {
        List<Long> relatedSkillIds = List.of(1L, 2L);
        List<Skill> relatedSkills = List.of(
                Skill.builder()
                        .id(1L)
                        .build(),
                Skill.builder()
                        .id(2L)
                        .build());
        Mockito.when(skillRepository.findAllById(relatedSkillIds)).thenReturn(relatedSkills);
        List<Skill> actualSkills = skillService.getRelatedSkills(relatedSkillIds);
        assertEquals(relatedSkills, actualSkills);
    }

}