package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlShortenRequest {
    @URL(message = "Invalid URL format")
    @NotBlank(message = "URL cannot be empty")
    private String url;
}