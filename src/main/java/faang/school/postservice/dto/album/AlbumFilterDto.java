package faang.school.postservice.dto.album;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumFilterDto {
    private String titlePattern;
    private List<Long> ids;
    private LocalDateTime from;
    private LocalDateTime to;
    private int page;
    private int pageSize;
}
