package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;

    @Transactional
    public SkillDto create(SkillDto skill) {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill entity = skillRepository.save(skillMapper.toEntity(skill));
            return skillMapper.toDto(entity);
        }
        throw new DataValidationException(ErrorMessage.SKILL_ALREADY_EXISTS, skill.getTitle());
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return !skills.isEmpty() ? skills.stream()
                .map(skillMapper::toDto)
                .toList() : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public boolean areExistingSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return true;
        }
        return skillRepository.countExisting(skillIds) == skillIds.size();
    }

    @Transactional(readOnly = true)
    public List<SkillCandidateDto> getOfferedSkills(long userId, int page, int pageSize) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills.stream()
                .skip((long) page * pageSize)
                .limit(pageSize)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId)
                .map(skillMapper::toDto)
                .orElseGet(() -> acquireSkill(skillId, userId));
    }

    @Transactional
    public SkillDto acquireSkill(long skillId, long userId) {
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() >= MIN_SKILL_OFFERS) {
            return skillRepository.findById(skillId)
                    .map(skill -> {
                        skillRepository.assignSkillToUser(skillId, userId);
                        //addGuarantees(skill, offers);
                        return skillMapper.toDto(skill);
                    }).orElseThrow(() -> new IllegalArgumentException("There is no skill with id " + skillId));
        }
        throw new DataValidationException(ErrorMessage.NOT_ENOUGH_SKILL_OFFERS, MIN_SKILL_OFFERS);
    }

    @Transactional
    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }
}