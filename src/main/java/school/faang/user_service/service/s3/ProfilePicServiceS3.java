package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.FileException;

import static school.faang.user_service.util.PicResizeUtil.cropPicture;
import static school.faang.user_service.util.PicResizeUtil.resizePictureToSmall;

@Service
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class ProfilePicServiceS3 implements ProfilePicService {
    private final AmazonS3 s3client;
    private final String bucketName;

    public ProfilePicServiceS3(AmazonS3 s3client, @Value("${services.s3.bucket-name}") String bucketName) {
        this.s3client = s3client;
        this.bucketName = bucketName;
    }

    @Override
    public void deleteFile(String keyName) {
        s3client.deleteObject(bucketName, keyName);
    }

    @Override
    public byte[] downloadFile(String keyName) {
        S3Object s3Object = s3client.getObject(bucketName, keyName);
        try {
            return s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new FileException(ErrorMessage.FILE_EXCEPTION);
        }
    }


    public String[] resizeAndSavePic(MultipartFile file) {
        String picFormat = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        String[] keys = new String[2];
        try {
            byte[] contentBig = cropPicture(file.getBytes(), picFormat);
            byte[] contentSmall = resizePictureToSmall(contentBig, picFormat);
            String keyBig = DigestUtils.md5Hex(contentBig);
            String keySmall = DigestUtils.md5Hex(contentSmall);
            uploadFile(keyBig, contentBig, picFormat);
            uploadFile(keySmall, contentSmall, picFormat);
            keys[0] = keyBig;
            keys[1] = keySmall;
        } catch (IOException e) {
            throw new FileException(ErrorMessage.FILE_EXCEPTION, file.getOriginalFilename());
        }
        return keys;
    }

    private void uploadFile(String key, byte[] content, String fileFormat){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        metadata.setContentType(fileFormat);
        s3client.putObject(bucketName, key, new ByteArrayInputStream(content), metadata);
    }
}
