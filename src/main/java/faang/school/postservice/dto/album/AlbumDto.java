package faang.school.postservice.dto.album;

import faang.school.postservice.model.Visibility;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumDto {
    private Long id;

    @NotNull
    @Length(min = 1, max = 256)
    private String title;

    @NotNull
    @Length(min = 1, max = 4096)
    private String description;

    @NotNull
    private Long authorId;

    @NotNull
    private Visibility visibility;
    private List<Long> watchers;
    private List<Long> postIds;
    private LocalDateTime createdAt;
}
