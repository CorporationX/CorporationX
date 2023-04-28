package school.faang.user_service.dto.event;

import lombok.Data;

@Data
public class EventFilterDto {
    private String titlePattern;
    private String startDatePattern;
    private String ownerPattern;
    private String locationPattern;
    private String skillsPattern;
    private int page;
    private int pageSize;
}