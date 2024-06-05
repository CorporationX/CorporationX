package school.faang.user_service.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3ClientConfig {
    @Value("{aws.accessKey}")
    private String accessKey;

    @Value("{aws.secretKey}")
    private String secretKey;

    @Value("{aws.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 amazonS3(@Value("{aws.accessKey}") String accessKey,
                             @Value("{aws.secretKey}") String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_NORTH_1)
                .build();
    }

    @Bean
    public String getBucketName(@Value("{aws.bucketName}") String bucketName) {
        return bucketName;
    }
}