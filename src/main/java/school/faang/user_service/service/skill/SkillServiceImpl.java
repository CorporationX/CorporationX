package school.faang.user_service.service.skill;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SkillServiceImpl implements SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;

    @Override
    public SkillDto create(Skill skill) {
        skillValidator.validateSkill(skill);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toDTO(savedSkill);
    }

    @Override
    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toDTOList(skills);
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        Map<SkillDto, Long> offeredSkillsCountingMap = skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .map(skillMapper::toDTO)
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        return offeredSkillsCountingMap.entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = findById(skillId);
        Optional<Skill> offeredSkill = skillRepository.findUserSkill(skillId, userId);
        if (offeredSkill.isEmpty()) {
            if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) >= MIN_SKILL_OFFERS) {
                setSkillGuarantors(skillId, userId, skill);
                skillRepository.assignSkillToUser(skillId, userId);
            }
        }
        return skillMapper.toDTO(skill);
    }

    @Override
    public Skill findById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String.format("skill with id=%d not found", skillId)));
    }

    @Override
    public void existsById(long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new NoSuchElementException(String.format("skill with id=%d not found", skillId));
        }
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
                    UserSkillGuarantee savedGuarantee = userSkillGuaranteeRepository.save(userSkillGuarantee);
                    if (skill.getGuarantees() == null) {
                        List<UserSkillGuarantee> guaranties = new ArrayList<>();
                        skill.setGuarantees(guaranties);
                    }
                    skill.getGuarantees().add(savedGuarantee);
                });
    }
}