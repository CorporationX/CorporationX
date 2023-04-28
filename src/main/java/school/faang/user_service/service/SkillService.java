package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillOfferRepository;
import school.faang.user_service.repository.SkillRepository;

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
            Skill entity = skillRepository.create(skill.getTitle());
            return skillMapper.toDto(entity);
        }
        throw new DataValidationException(ErrorMessage.SKILL_ALREADY_EXISTS, skill.getTitle());
    }

    @Transactional
    public void delete(long id) {
        skillRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<SkillDto> getUserSkills(long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return skillRepository.findAllByUserId(userId, pageable).map(skillMapper::toDto);
    }

    @Transactional(readOnly = true)
    public boolean areExistingSkills(List<Long> skillIds) {
        if (skillIds.isEmpty()) {
            return true;
        }
        return skillRepository.countExisting(skillIds) == skillIds.size();
    }

    @Transactional(readOnly = true)
    public List<SkillCandidateDto> getOfferedSkills(long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Skill> skills = skillRepository.findSkillsOfferedToUser(userId, pageable);
        return skills.get()
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

    private SkillDto acquireSkill(long skillId, long userId) {
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() >= MIN_SKILL_OFFERS) {
            return skillRepository.findById(skillId)
                    .map(skill -> {
                        skillRepository.assignSkillToUser(skillId, userId);
                        addGuarantees(skill, offers);
                        return skillMapper.toDto(skill);
                    }).orElseThrow(() -> new IllegalArgumentException("There is no skill with id " + skillId));
        }
        throw new DataValidationException(ErrorMessage.NOT_ENOUGH_SKILL_OFFERS, MIN_SKILL_OFFERS);
    }

    private void addGuarantees(Skill skill, List<SkillOffer> offers) {
        List<User> guarantors = offers.stream()
                .map(SkillOffer::getRecommendation)
                .map(Recommendation::getAuthor)
                .toList();
        skill.addGuarantees(guarantors);
    }
}