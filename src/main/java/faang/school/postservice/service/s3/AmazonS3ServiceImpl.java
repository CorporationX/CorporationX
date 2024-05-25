package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.S3Exception;
import faang.school.postservice.property.AmazonS3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final AmazonS3 amazonS3;
    private final AmazonS3Properties amazonS3Properties;

    @Override
    public String uploadFile(MultipartFile file) {

        String key = generateUniqueKey();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(amazonS3Properties.getBucketName(), key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new S3Exception("Cant upload file to s3 storage");
        }

        return key;
    }

    @Override
    public InputStream downloadFile(String key) {
        return amazonS3.getObject(amazonS3Properties.getBucketName(), key).getObjectContent();
    }

    @Override
    public void deleteFile(String key) {
        amazonS3.deleteObject(amazonS3Properties.getBucketName(), key);
    }

    private String generateUniqueKey() {
        return UUID.randomUUID().toString();
    }
}
