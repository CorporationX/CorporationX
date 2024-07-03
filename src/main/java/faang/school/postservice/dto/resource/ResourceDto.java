package faang.school.postservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {
    private Long id;
    private String key;
    private long size;
    private LocalDateTime createdAt;
    private String name;
    private String type;
}
