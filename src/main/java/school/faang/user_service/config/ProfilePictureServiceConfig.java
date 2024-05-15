package school.faang.user_service.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Data
@Configuration
public class ProfilePictureServiceConfig {
    @Value("${diceBear-picture-service.diceBear-url}")
    private String pictureURLWithoutStyle;
    
    @Value("#{'${diceBear-picture-service.styles}'.split(',')}")
    private Set<String> styles;

    @Value("${diceBear-picture-service.extensions.original}")
    private String extensionOfOriginalFile;

    @Value("${diceBear-picture-service.extensions.decreased}")
    private String extensionOfDecreasedFile;

    @Value("#{new Integer('${diceBear-picture-service.file-width}')}")
    private int widthOfDecreasedFile;

    @Value("#{new Integer('${diceBear-picture-service.file-height}')}")
    private int heightOfDecreasedFile;

    private String pictureUrlWithAllSettings;

    @PostConstruct
    public void initialize() {
        pictureUrlWithAllSettings = pictureURLWithoutStyle + styles.iterator().next() + "/svg";
    }
}