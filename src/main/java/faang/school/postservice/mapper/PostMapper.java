package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(PostDto dto);

    PostDto toDto(Post entity);

    void update(@MappingTarget Post entity, PostDto dto);
}
