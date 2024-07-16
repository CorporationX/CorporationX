package faang.school.postservice.mapper.comment;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.comment.CommentToCreateDto;
import faang.school.postservice.entity.dto.comment.CommentToUpdateDto;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.entity.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentToCreateDto commentDto);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    void update(CommentToUpdateDto commentDto, @MappingTarget Comment comment);

    @Named("getCountFromLikeList")
    default int getCountFromLikeList(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }

    CommentDto toDto(NewCommentEvent newCommentEvent);
}
