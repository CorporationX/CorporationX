package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(PostCreateDto postCreateDto);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    PostDto toDto(Post post);

    @Named("getCountFromLikeList")
    default int getCountFromLikeList(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }
}
