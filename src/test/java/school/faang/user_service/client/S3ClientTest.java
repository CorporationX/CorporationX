package school.faang.user_service.client;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ClientTest {
    private static final long USER_ID = 4L;
    private static final String FILE_EXTENSION = ".jpg";
    @Mock
    private AmazonS3 amazonS3;
    @InjectMocks
    private S3Client s3Client;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
    }

    @Test
    public void whenUploadingProfilePictureSuccessfully() {
        byte[] pictureData = {};
        s3Client.uploadProfilePicture(user, pictureData, FILE_EXTENSION);
        verify(amazonS3).putObject(eq(s3Client.getBucketName()), eq(USER_ID + FILE_EXTENSION), any(), any());
    }

    @Test
    public void whenGeneratePictureLinkSuccessfully() throws MalformedURLException {
        String expectedURL = "https://url.com/pic.jpg";
        when(amazonS3.generatePresignedUrl(any())).thenReturn(new URL(expectedURL));
        String actualUrl = s3Client.generatePictureLink(USER_ID, FILE_EXTENSION);
        assertThat(actualUrl).isEqualTo(expectedURL);
    }
}