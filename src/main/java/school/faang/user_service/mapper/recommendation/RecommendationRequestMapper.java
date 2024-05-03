package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillOffersToSkillIds")
    RecommendationRequestDto fromEntityToDto(RecommendationRequest recommendationRequest);
    List<RecommendationRequestDto> fromEntityListToDtoList(List<RecommendationRequest> recommendationRequests);

    @Named("mapSkillOffersToSkillIds")
    default List<Long> mapSkillOffersToSkillIds(List<SkillRequest> skillRequests) {
        return skillRequests.stream().map(skillRequest -> skillRequest.getSkill().getId()).toList();
    }
}