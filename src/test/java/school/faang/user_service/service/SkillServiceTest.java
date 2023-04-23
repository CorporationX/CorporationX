package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SkillRepository skillRepository;

    @Test
    void createSkill() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("SQL");

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("SQL");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(Boolean.FALSE);
        when(skillRepository.create(skillDto.getTitle())).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result);
        assertEquals(skillDto.getTitle(), result.getTitle());
        verify(skillRepository).existsByTitle(skillDto.getTitle());
        verify(skillRepository).create(skillDto.getTitle());
        verify(skillMapper).toDto(skill);
    }

    @Test
    void createSkill_EmptyTitleDataValidationExceptionTest() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("SQL");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(Boolean.TRUE);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
                skillService.create(skillDto)
        );

        assertEquals("A skill with the title "  + skillDto.getTitle() +  " already exists", dataValidationException.getMessage());
    }
}