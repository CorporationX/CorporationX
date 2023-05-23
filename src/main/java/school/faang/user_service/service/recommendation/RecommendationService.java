package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserSkillGuaranteeDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillCandidateValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationRequestService recommendationRequestService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final RecommendationValidator recommendationValidator;
    private final SkillCandidateValidator skillCandidateValidator;
    private final SkillService skillService;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validate(recommendation);
        long entityId = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent()
        );
        Recommendation entity = recommendationRepository.findById(entityId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.RECOMMENDATION_NOT_FOUND, entityId));
        saveSkillOffers(entity, recommendation.getSkillOffers());
        recommendationRequestService.acceptRequestIfNecessary(entity);
        return recommendationMapper.toDto(entity);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendation) {
        validate(recommendation);
        Recommendation entity = recommendationRepository.update(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent()
        );
        skillOfferRepository.deleteAllByRecommendationId(entity.getId());
        saveSkillOffers(entity, recommendation.getSkillOffers());
        return recommendationMapper.toDto(entity);
    }

    @Transactional
    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllUserRecommendations(long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return recommendationRepository.findAllByReceiverId(userId, pageable)
                .map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllGivenRecommendations(long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return recommendationRepository.findAllByAuthorId(userId, pageable)
                .map(recommendationMapper::toDto);
    }

    private void validate(RecommendationDto recommendation) {
        recommendationValidator.validate(recommendation);
        skillCandidateValidator.validate(recommendation.getSkillOffers());
    }

    @Transactional
    public void saveSkillOffers(Recommendation entity, List<SkillOfferDto> skillOffers) {
        skillOffers.forEach(offer -> {
            long savedOfferId = skillOfferRepository.create(offer.getSkillId(), entity.getId());
            entity.addSkillOffer(skillOfferRepository.findById(savedOfferId).orElse(null));
            provideGuaranteesIfSkillExists(entity);
        });
    }

    @Transactional
    public void provideGuaranteesIfSkillExists(Recommendation recommendation) {
        List<SkillDto> userSkills = skillService.getUserSkills(recommendation.getReceiver().getId());
        UserSkillGuaranteeDto userSkillGuaranteeDto = getUserSkillGuarantee(recommendation);

        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            userSkills.stream()
                    .filter(userSkill -> userSkill.getId().equals(skillOffer.getSkill().getId()))
                    .forEach(skill -> provideGuarantee(skill, recommendation.getAuthor().getId(), userSkillGuaranteeDto));
        }
    }

    private UserSkillGuaranteeDto getUserSkillGuarantee(Recommendation recommendation) {
        UserSkillGuaranteeDto userSkillGuaranteeDto = new UserSkillGuaranteeDto();
        userSkillGuaranteeDto.setUserId(recommendation.getReceiver().getId());
        userSkillGuaranteeDto.setGuarantorId(recommendation.getAuthor().getId());
        return userSkillGuaranteeDto;
    }

    @Transactional
    public void provideGuarantee(SkillDto skill, long authorId, UserSkillGuaranteeDto userSkillGuaranteeDto) {
        if (skill.getGuarantees().stream()
                .noneMatch(guarantee -> guarantee.getGuarantorId() == authorId)) {
            saveUserSkillGuarantee(userSkillGuaranteeDto, skill);
        }
    }

    @Transactional
    public void saveUserSkillGuarantee(UserSkillGuaranteeDto userSkillGuaranteeDto, SkillDto skillDto) {
        userSkillGuaranteeDto.setSkillId(skillDto.getId());
        skillDto.getGuarantees().add(userSkillGuaranteeDto);
        userSkillGuaranteeRepository.save(userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto));
    }
}