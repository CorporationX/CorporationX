package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private SkillValidator skillValidator;
    @InjectMocks
    private SkillServiceImpl skillService;
    private Skill skill;

    @BeforeEach
    public void srtUp() {
        skill = new Skill();
        skill.setId(1L);
        skill.setTitle("firstSkill");
    }

    @Test
    public void whenCreateSkillWithBlankTitleAndThrowsException() {
        when(skillValidator.validateSkill(skill)).thenThrow(DataValidationException.class);
        Assert.assertThrows(DataValidationException.class,
                () -> skillService.create(skill));
    }

    @Test
    public void whenCreateSkillSuccessfully() {
        SkillDto expected = new SkillDto(skill.getId(), skill.getTitle());
        when(skillValidator.validateSkill(skill)).thenReturn(true);
        when(skillMapper.toDTO(skill)).thenReturn(expected);
        when(skillRepository.save(skill)).thenReturn(skill);
        SkillDto actual = skillService.create(skill);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenGetAllUserSkillsByIdSuccessfully() {
        long userId = 3L;
        SkillDto skillDto = new SkillDto(skill.getId(), skill.getTitle());
        List<Skill> skills = List.of(skill);
        List<SkillDto> expectedSkills = List.of(skillDto);
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(skills);
        when(skillMapper.toDTOList(skills)).thenReturn(expectedSkills);
        List<SkillDto> actualSkills = skillService.getUserSkills(userId);
        assertThat(actualSkills).isEqualTo(expectedSkills);
    }

    @Test
    public void whenGetOfferedUserSkillsSuccessfully() {
        int offersAmount = 2;
        long userId = 3L;
        SkillDto skillDto = new SkillDto(skill.getId(), skill.getTitle());
        List<Skill> offeredSkills = List.of(skill, skill);
        when(skillRepository.findSkillsOfferedToUser(anyLong())).thenReturn(offeredSkills);
        when(skillMapper.toDTO(skill)).thenReturn(skillDto);
        List<SkillCandidateDto> expectedOfferedSkills = List.of(new SkillCandidateDto(skillDto, offersAmount));
        List<SkillCandidateDto> actualOfferedSkills = skillService.getOfferedSkills(userId);
        assertThat(actualOfferedSkills).isEqualTo(expectedOfferedSkills);
    }

    @Test
    public void whenFindByIdThenThrowsException() {
        when(skillRepository.findById(anyLong())).thenThrow(NoSuchElementException.class);
        Assert.assertThrows(NoSuchElementException.class,
                () -> skillService.findById(skill.getId()));
    }

    @Test
    public void whenFindByIdThenGetSkill() {
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
        Skill actual = skillService.findById(skill.getId());
        assertThat(actual).isEqualTo(skill);
    }

    @Test
    public void whenAcquireSkillFromOffersThenThrowsException() {
        Assert.assertThrows(NoSuchElementException.class,
                () -> skillService.acquireSkillFromOffers(0L, 0L));
    }

    @Test
    public void whenAcquireSkillFromOffersSuccessfully() {
        long skillId = skill.getId();
        long userId = 1L;
        int offersAmount = 3;
        SkillDto expected = new SkillDto(skillId, skill.getTitle());
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillOfferRepository.countAllOffersOfSkill(skillId, userId)).thenReturn(offersAmount);
        when(skillMapper.toDTO(skill)).thenReturn(expected);
        SkillDto actual = skillService.acquireSkillFromOffers(skillId, userId);
        assertThat(actual).isEqualTo(expected);
    }
}