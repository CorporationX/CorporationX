package school.faang.user_service.service.s3;

import org.springframework.web.multipart.MultipartFile;

public interface ProfilePicService {
    String[] resizeAndSavePic(MultipartFile file);
    void deleteFile(String keyName);
    byte[] downloadFile(String keyName);
}