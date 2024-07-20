package faang.school.postservice.entity.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PostDto implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private HashSet<Long> likesIds;
    private HashSet<Long> viewersIds;
    private boolean published;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime publishedAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
