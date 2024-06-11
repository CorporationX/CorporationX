package faang.school.postservice.config.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import faang.school.postservice.property.AmazonS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmazonS3Config {

    private final AmazonS3Properties amazonS3Properties;

    @Bean
    public AmazonS3 s3Client() {

        AWSCredentials awsCredentials = new BasicAWSCredentials(amazonS3Properties.getAccessKey(), amazonS3Properties.getSecretKey());

        AmazonS3 clientAmazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonS3Properties.getEndpoint(), null))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        if (!clientAmazonS3.doesBucketExistV2(amazonS3Properties.getBucketName())) {
            clientAmazonS3.createBucket(amazonS3Properties.getBucketName());
        }

        return clientAmazonS3;
    }
}
