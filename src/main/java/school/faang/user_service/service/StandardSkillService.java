package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class StandardSkillService implements SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillDto create(Skill skill) {
        validateSkill(skill);
        Skill savingSkill = skillRepository.save(skill);
        return skillMapper.fromSkillToSkillDto(savingSkill);
    }

    @Override
    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.fromSkillListToSkillDtoList(skills);
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<SkillDto, Long> offeredSkillsCountingMap = new HashMap<>();
        offeredSkills.stream()
                .map(skillMapper::fromSkillToSkillDto)
                .forEach(skillDto -> offeredSkillsCountingMap.compute(skillDto, (key, value) -> value == null ? 1L : value + 1));
        return offeredSkillsCountingMap.entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String.format("skill with id=%d not found", skillId)));
        Optional<Skill> offeredSkill = skillRepository.findUserSkill(skillId, userId);
        if (offeredSkill.isEmpty()) {
            if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) >= MIN_SKILL_OFFERS) {
                setSkillGuarantors(skillId, userId, skill);
                skillRepository.assignSkillToUser(skillId, userId);
            }
        }
        return skillMapper.fromSkillToSkillDto(skill);
    }

    private void setSkillGuarantors(long skillId, long userId, Skill skill) {
        skillOfferRepository.findAllOffersOfSkill(skillId, userId)
                .forEach(skillOffer -> {
                    User guarantor = skillOffer.getRecommendation().getAuthor();
                    User receiver = skillOffer.getRecommendation().getReceiver();
                    UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                    userSkillGuarantee.setUser(receiver);
                    userSkillGuarantee.setSkill(skill);
                    userSkillGuarantee.setGuarantor(guarantor);
                    UserSkillGuarantee savingGuarantee = userSkillGuaranteeRepository.save(userSkillGuarantee);
                    if (skill.getGuarantees() == null) {
                        List<UserSkillGuarantee> guaranties = new ArrayList<>();
                        skill.setGuarantees(guaranties);
                    }
                    skill.getGuarantees().add(savingGuarantee);
                });
    }

    private void validateSkill(Skill skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("please enter the title of the skill");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(String.format("skill \"%s\" already exists", skill.getTitle()));
        }
    }
}