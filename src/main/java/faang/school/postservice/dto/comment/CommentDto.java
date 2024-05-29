package faang.school.postservice.dto.comment;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDto {

    private String content;
    private long authorId;
    private long likesCount;
    private long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}