package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    Post toEntity(PostCreateDto postCreateDto);

    @Mapping(target = "likesCount", expression = "java(post.getLikes() == null ? post.getLikes().size() : 0)")
    PostDto toDto(Post post);
}
