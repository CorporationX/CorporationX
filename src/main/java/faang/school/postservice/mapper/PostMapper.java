package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(PostDto dto);

    PostDto toDto(Post entity);

    @Mapping(target = "likes", source = "likes")
    PostDto toDtoWithLikes(Post entity, int likes);

    void update(@MappingTarget Post entity, PostDto dto);
}
