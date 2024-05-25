package faang.school.postservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services.s3")
@Data
public class AmazonS3Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private int targetWidth;
    private int targetHeight;
    private int maxFilesAmount;
}
