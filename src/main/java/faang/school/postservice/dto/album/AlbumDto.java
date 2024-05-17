package faang.school.postservice.dto.album;

import faang.school.postservice.model.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class AlbumDto {

    @NotEmpty
    @NotBlank
    private String title;

    @NotEmpty
    @NotBlank
    private String description;

    @Positive
    private long authorId;

    private List<Post> posts;
}
