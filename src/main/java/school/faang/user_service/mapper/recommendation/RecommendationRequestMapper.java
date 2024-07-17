package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillRequestToSkillIds")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto ToDto(RecommendationRequest recommendationRequest);

    List<RecommendationRequestDto> ToDtoList(List<RecommendationRequest> recommendationRequests);

    @Mapping(source = "skillIds", target = "skills", qualifiedByName = "mapSkillIdsToSkillRequest")
    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    RecommendationRequest ToEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapSkillRequestToSkillIds")
    default List<Long> mapSkillOffersToSkillIds(List<SkillRequest> skillRequests) {
        return skillRequests.stream()
                .map(skillRequest -> skillRequest.getSkill().getId()).toList();
    }

    @Named("mapSkillIdsToSkillRequest")
    default List<SkillRequest> mapSkillIdsToSkillRequest(List<Long> skillIds) {
        return skillIds.stream()
                .map(skillId ->  {
            SkillRequest skillRequest = new SkillRequest();
            Skill skill = new Skill();
            skill.setId(skillId);
            skillRequest.setSkill(skill);
            return skillRequest;
        }).toList();
    }
}