package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillCandidateValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationRequestService recommendationRequestService;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final SkillCandidateValidator skillCandidateValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validate(recommendation);
        Recommendation entity = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent()
        );
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

    private void saveSkillOffers(Recommendation entity, List<SkillOfferDto> skillOffers) {
        skillOffers.forEach(offer -> {
            SkillOffer savedOffer = skillOfferRepository.create(offer.getSkillId(), entity.getId());
            entity.addSkillOffer(savedOffer);
            provideGuaranteesIfSkillExists(entity, skillOffers);
        });
    }

    private void provideGuaranteesIfSkillExists(Recommendation entity, List<SkillOfferDto> skillOffers) {
        Set<Skill> skills = new HashSet<>(entity.getReceiver().getSkills());
        skillOffers.forEach(offer -> skills.stream()
                .filter(skill -> skill.getId() == offer.getSkillId())
                .findFirst()
                .ifPresent(skill -> {
                    if (!skill.getGuarantees().contains(entity.getAuthor())) {
                        skill.addGuarantee(entity.getAuthor());
                    }
                }));
    }
}
