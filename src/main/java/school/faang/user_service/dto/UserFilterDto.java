package school.faang.user_service.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


@Data
@NoArgsConstructor
public class UserFilterDto {
    private String usernamePattern;
    private String aboutPattern;
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    private Integer experienceMin;
    private Integer experienceMax;

    @Value("${page:1}")
    private int page;

    @Value("${page:10}")
    private int pageSize;
}