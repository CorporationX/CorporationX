package faang.school.postservice.dto.album;

import lombok.Data;

@Data
public class AlbumFilterDto {
    private String titlePattern;
    private String fromDate;
    private String beforeDate;
}
