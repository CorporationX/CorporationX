package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostHashtagDto;
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

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "getIdFromLike")
    PostHashtagDto toHashtagDto(Post post);

    @Mapping(source = "likeIds", target = "likes", qualifiedByName = "getLikeFromId")
    Post toEntity(PostHashtagDto post);

    @Mapping(source = "likeIds", target = "likesCount", qualifiedByName = "getCountFromList")
    PostDto toDto(PostHashtagDto post);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    PostDto toDto(Post post);

    @Named("getCountFromLikeList")
    default int getCountFromLikeList(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }

    @Named("getCountFromList")
    default int getCountFromList(List<Long> ids) {
        return ids != null ? ids.size() : 0;
    }

    @Named("getIdFromLike")
    default long getIdFromLike(Like like) {
        return like != null ? like.getId() : 0;
    }

    @Named("getLikeFromId")
    default Like getLikeFromId(Long id) {
        return Like.builder().id(id).build();
    }
}
