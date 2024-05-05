package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillValidator skillValidator;
    private Skill skill;

    @BeforeEach
    public void setUp() {
        skill = new Skill();
    }


    @Test
    public void whenValidateSkillWithNullTitleThenThrowsException() {
        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skill));
    }

    @Test
    public void whenValidateSkillWithBlankTitleThenThrowsException() {
        skill.setTitle("  ");
        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skill));
    }

    @Test
    public void whenValidateExistingSkillThenThrowsException() {
        skill.setTitle("title");
        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(true);
        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skill));
    }

    @Test
    public void whenValidateExistingSkillSuccessfully() {
        skill.setTitle("title");
        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(false);
        assertThat(skillValidator.validateSkill(skill)).isTrue();
    }
}