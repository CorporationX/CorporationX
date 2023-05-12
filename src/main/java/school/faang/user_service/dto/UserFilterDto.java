package school.faang.user_service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserFilterDto {
    @Size(max = 255, message = "no more than 255 characters for namePattern")
    private String namePattern;
    @Size(max = 255, message = "no more than 255 characters for aboutPattern")
    private String aboutPattern;
    @Size(max = 255, message = "no more than 255 characters for emailPattern")
    private String emailPattern;
    @Size(max = 255, message = "no more than 255 characters for contactPattern")
    private String contactPattern;
    @Size(max = 255, message = "no more than 255 characters for countryPattern")
    private String countryPattern;
    @Size(max = 255, message = "no more than 255 characters for cityPattern")
    private String cityPattern;
    @Size(max = 255, message = "no more than 255 characters for phonePattern")
    private String phonePattern;
    @Size(max = 255, message = "no more than 255 characters for skillPattern")
    private String skillPattern;
    private int experienceMin;
    private int experienceMax;
    private int page;
    private int pageSize;
}
