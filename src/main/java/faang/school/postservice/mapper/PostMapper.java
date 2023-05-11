package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likes", ignore = true)
    Post toEntity(PostDto dto);

    @Mapping(target = "likes", ignore = true)
    PostDto toDto(Post entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likes", ignore = true)
    void update(@MappingTarget Post entity, PostDto dto);
}
