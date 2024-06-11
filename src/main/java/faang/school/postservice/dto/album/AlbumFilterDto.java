package faang.school.postservice.dto.album;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlbumFilterDto {
    @Size(max = 255, message = "title pattern should be less then 255 characters")
    private String titlePattern;
    private String fromDate;
    private String beforeDate;
}
