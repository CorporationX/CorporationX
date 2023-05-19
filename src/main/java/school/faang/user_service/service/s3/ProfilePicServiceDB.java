package school.faang.user_service.service.s3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.ContentData;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.repository.ContentDataRepository;

import static school.faang.user_service.util.PicResizeUtil.cropPicture;
import static school.faang.user_service.util.PicResizeUtil.resizePictureToSmall;

@Service
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "true")
public class ProfilePicServiceDB implements ProfilePicService {
    private final ContentDataRepository contentDataRepository;

    public ProfilePicServiceDB(ContentDataRepository contentDataRepository) {
        this.contentDataRepository = contentDataRepository;
    }

    @Override
    public String[] resizeAndSavePic(MultipartFile file) {
        String picFormat = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        String[] keys = new String[2];
        try {
            byte[] contentBig = cropPicture(file.getBytes(), picFormat);
            byte[] contentSmall = resizePictureToSmall(contentBig, picFormat);
            ContentData entityBig = new ContentData();
            entityBig.setContent(contentBig);
            ContentData entitySmall = new ContentData();
            entityBig.setContent(contentSmall);
            keys[0] = String.valueOf(contentDataRepository.save(entityBig).getId());
            keys[1] = String.valueOf(contentDataRepository.save(entitySmall).getId());
        } catch (IOException e) {
            throw new FileException(ErrorMessage.FILE_EXCEPTION, file.getOriginalFilename());
        }
        return keys;
    }

    @Override
    public void deleteFile(String keyName) {
        long id = Long.parseLong(keyName);
        ContentData entity = contentDataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("content by id %s not found", keyName)));

        contentDataRepository.delete(entity);
    }

    @Override
    public byte[] downloadFile(String keyName) {
        long id = Long.parseLong(keyName);
        ContentData entity = contentDataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("content by id %s not found", keyName)));
        if (Objects.isNull(entity.getContent())) {
            throw new FileException(String.format("file by id %s is empty", keyName));
        }

        return new ByteArrayInputStream(entity.getContent()).readAllBytes();
    }
}
