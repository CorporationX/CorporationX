package school.faang.user_service.service.skill;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {
    private static final long SKILL_ID = 1L;

    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillServiceImpl skillService;
    private Skill skill;

    @BeforeEach
    public void setUp() {
        skill = new Skill();
        skill.setId(SKILL_ID);
    }



    @Test
    public void whenSkillNotExistsByIdThenThrowsException() {
        when(skillRepository.existsById(SKILL_ID)).thenReturn(false);
        Assert.assertThrows(NoSuchElementException.class,
                () -> skillService.existsById(SKILL_ID));
    }


    @Test
    public void whenSkillExistsByIdThenNoException() {
        when(skillRepository.existsById(SKILL_ID)).thenReturn(true);
        assertThat(skillService.existsById(SKILL_ID)).isTrue();
    }
}