package school.faang.user_service.mapper.recommendation;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(target = "author", qualifiedByName = "mapAuthor", source = "authorId")
    @Mapping(target = "receiver", qualifiedByName = "mapReceiver", source = "receiverId")
    Recommendation toEntity(RecommendationDto dto);

    @Named("mapAuthor")
    default User mapAuthor(Long authorId) {
        if (authorId == null) {
            return null;
        }
        User author = new User();
        author.setId(authorId);
        return User.builder()
                .id(authorId).build();
    }

    @Named("mapReceiver")
    default User mapReceiver(Long receiverId) {
        if (receiverId == null) {
            return null;
        }
        return User.builder()
                .id(receiverId).build();
    }

    RecommendationDto toDto(Recommendation entity);
}