package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.RecommendationRepository;
import school.faang.user_service.repository.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillOfferValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final SkillOfferValidator skillOfferValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validate(recommendation);
        Recommendation entity = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent()
        );
        saveSkillOffers(entity, recommendation.getSkillOffers());
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
        skillOfferValidator.validate(recommendation.getSkillOffers());
    }

    private void saveSkillOffers(Recommendation entity, List<SkillOfferDto> skillOffers) {
        skillOffers.forEach(offer -> {
            SkillOffer savedOffer = skillOfferRepository.create(offer.getSkillId(), entity.getId());
            entity.addSkillOffer(savedOffer);
        });
    }
}
