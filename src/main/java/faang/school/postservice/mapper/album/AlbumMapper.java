package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mappings({
            @Mapping(target = "postIds", source = "posts", qualifiedByName = "postsToPostIds")
    })
    AlbumDto toDto(Album album);

    @Mappings({
            @Mapping(target = "posts", source = "postIds", qualifiedByName = "postIdsToPosts")
    })
    Album toEntity(AlbumDto albumDto);

    @Mappings({
            @Mapping(target = "posts", source = "dto.postIds", qualifiedByName = "postIdsToPosts")
    })
    void update(AlbumDto dto, @MappingTarget Album entity);

    @Named("postIdsToPosts")
    default List<Post> postIdsToPosts(List<Long> postIds) {
        return postIds.stream()
                .map(id -> {
                    Post post = new Post();
                    post.setId(id);
                    return post;
                })
                .collect(Collectors.toList());
    }

    @Named("postsToPostIds")
    default List<Long> postsToPostIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }
}