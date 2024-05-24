package school.faang.user_service.client;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.io.ByteArrayInputStream;

@Data
@Slf4j
@Component
@AllArgsConstructor
public class S3Client {
    private final AmazonS3 amazonS3;
    private final String bucketName;

    public void uploadProfilePicture(User user, byte[] pictureData, String pictureExtension) {
        log.info("Uploading picture for user with id {} into bucket {}", user.getId(), bucketName);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(pictureData.length);
            amazonS3.putObject(bucketName, user.getId() + pictureExtension, new ByteArrayInputStream(pictureData), metadata);
            log.info("Picture uploaded successfully");
        } catch (AmazonServiceException e) {
            log.error("Uploading failed: {}", e.getErrorMessage());
        }
    }

    public String generatePictureLink(Long id, String pictureExtension) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, id + pictureExtension);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}