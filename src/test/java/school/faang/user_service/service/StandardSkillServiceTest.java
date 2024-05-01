package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StandardSkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillMapper skillMapper;
    @InjectMocks
    private StandardSkillService skillService;

    @Test
    public void whenCreateSkillWithBlankTitleThenThrowsException() {
        Assert.assertThrows(DataValidationException.class,
                () -> skillService.create(new Skill()));
    }

    @Test
    public void whenCreateExistedSkillThenThrowsException() {
        Skill creatingSkill = new Skill();
        creatingSkill.setTitle("title");
        when(skillRepository.existsByTitle(creatingSkill.getTitle())).thenReturn(true);
        Assert.assertThrows(DataValidationException.class,
                () -> skillService.create(creatingSkill));
    }

    @Test
    public void whenCreateSkillSuccessfully() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        SkillDto expected = new SkillDto(skill.getId(), skill.getTitle());
        when(skillMapper.fromSkillToSkillDto(skill)).thenReturn(expected);
        when(skillRepository.save(skill)).thenReturn(skill);
        SkillDto actual = skillService.create(skill);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenGetAllUserSkillsByIdSuccessfully() {
        long userId = 3L;
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("firstSkill");
        SkillDto skillDto = new SkillDto(skill.getId(), skill.getTitle());
        List<Skill> skills = List.of(skill);
        List<SkillDto> expectedSkills = List.of(skillDto);
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(skills);
        when(skillMapper.fromSkillListToSkillDtoList(skills)).thenReturn(expectedSkills);
        List<SkillDto> actualSkills = skillService.getUserSkills(userId);
        assertThat(actualSkills).isEqualTo(expectedSkills);
    }

    @Test
    public void whenGetOfferedUserSkillsSuccessfully() {
        int offersAmount = 2;
        long userId = 3L;
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("firstSkill");
        SkillDto skillDto = new SkillDto(skill.getId(), skill.getTitle());
        List<Skill> offeredSkills = List.of(skill, skill);
        when(skillRepository.findSkillsOfferedToUser(anyLong())).thenReturn(offeredSkills);
        when(skillMapper.fromSkillToSkillDto(skill)).thenReturn(skillDto);
        List<SkillCandidateDto> expectedOfferedSkills = List.of(new SkillCandidateDto(skillDto, offersAmount));
        List<SkillCandidateDto> actualOfferedSkills = skillService.getOfferedSkills(userId);
        assertThat(actualOfferedSkills).isEqualTo(expectedOfferedSkills);
    }

    @Test
    public void whenAcquireSkillFromOffersThenThrowsException() {
        Assert.assertThrows(NoSuchElementException.class,
                () -> skillService.acquireSkillFromOffers(0L, 0L));
    }

    @Test
    public void whenAcquireSkillFromOffersSuccessfully() {
        long skillId = 1L;
        long userId = 1L;
        int offersAmount = 3;
        Skill skill = new Skill();
        skill.setId(skillId);
        skill.setTitle("title");
        SkillDto expected = new SkillDto(skill.getId(), skill.getTitle());
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillOfferRepository.countAllOffersOfSkill(skillId, userId)).thenReturn(offersAmount);
        when(skillMapper.fromSkillToSkillDto(skill)).thenReturn(expected);
        SkillDto actual = skillService.acquireSkillFromOffers(skillId, userId);
        assertThat(actual).isEqualTo(expected);
    }
}