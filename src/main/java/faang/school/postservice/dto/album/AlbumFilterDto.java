package faang.school.postservice.dto.album;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AlbumFilterDto {

    private String titlePattern;
    private String fromDate;
    private String beforeDate;
}
