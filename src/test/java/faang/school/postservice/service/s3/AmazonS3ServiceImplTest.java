package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.postservice.property.AmazonS3Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private AmazonS3Properties amazonS3Properties;
    @InjectMocks
    private AmazonS3ServiceImpl amazonS3ServiceImpl;

    @Test
    void successUploadFile() {
        MultipartFile file = mock(MultipartFile.class);
        String uuid = amazonS3ServiceImpl.uploadFile(file);

        assertNotNull(UUID.fromString(uuid));

        verify(amazonS3, times(1)).putObject(any(), any(), any(), any());
        verify(amazonS3Properties, times(1)).getBucketName();
    }

    @Test
    void successDownloadFile() {
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStreamMock = mock(S3ObjectInputStream.class);

        when(amazonS3.getObject(any(), anyString())).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStreamMock);

        amazonS3ServiceImpl.downloadFile(UUID.randomUUID().toString());

        verify(amazonS3, times(1)).getObject(any(), anyString());
        verify(s3Object, times(1)).getObjectContent();
    }

    @Test
    void successDeleteFile() {
        amazonS3ServiceImpl.deleteFile(UUID.randomUUID().toString());

        verify(amazonS3, times(1)).deleteObject(any(), anyString());
    }
}
