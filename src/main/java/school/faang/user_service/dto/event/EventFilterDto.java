package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class EventFilterDto {
    @Size(max = 255, message = "no more than 255 characters for titlePattern")
    private String titlePattern;
    private LocalDateTime since;
    private LocalDateTime till;
    @Size(max = 255, message = "no more than 255 characters for ownerPattern")
    private String ownerPattern;
    @Size(max = 255, message = "no more than 255 characters for ownerIdPattern")
    private long ownerIdPattern;
    @Size(max = 255, message = "no more than 255 characters for locationPattern")
    private String locationPattern;
    @Size(max = 255, message = "no more than 255 characters for skillsPattern")
    private String skillsPattern;
    private List<Long> skillIds;
    private String type;
    private String status;
    private int page;
    private int pageSize;
}