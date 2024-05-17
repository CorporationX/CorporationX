package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mappings({
            @Mapping(source = "album.title", target = "title"),
            @Mapping(source = "album.description", target = "description"),
            @Mapping(target = "postIds", expression = "java(postsToPostIds(album.getPosts()))")
    })
    AlbumDto toDto(Album album);

    @Mappings({
            @Mapping(source = "albumDto.title", target = "title"),
            @Mapping(source = "albumDto.description", target = "description"),
            @Mapping(target = "posts", expression = "java(postIdsToPosts(albumDto.getPostIds()))")
    })
    Album toEntity(AlbumDto albumDto);

    @Mappings({
            @Mapping(source = "dto.title", target = "title"),
            @Mapping(source = "dto.description", target = "description"),
            @Mapping(target = "posts", expression = "java(postIdsToPosts(dto.getPostIds()))")
    })
    void update(AlbumDto dto, @MappingTarget Album entity);

    default List<Post> postIdsToPosts(List<Long> postIds) {
        return postIds.stream()
                .map(id -> {
                    Post post = new Post();
                    post.setId(id);
                    return post;
                })
                .collect(Collectors.toList());
    }

    default List<Long> postsToPostIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }
}