package faang.school.postservice.service.moderation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @BeforeEach
    void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {

        moderationDictionary = new ModerationDictionary(resourceLoader);
        Field field = ModerationDictionary.class.getDeclaredField("filepath");
        field.setAccessible(true);
        field.set(moderationDictionary, "classpath:bad-words.txt");
        InputStream inputStream = new ByteArrayInputStream("badword1\nbadword2\nbadword3".getBytes());

        doReturn(resource).when(resourceLoader).getResource("classpath:bad-words.txt");
        doReturn(inputStream).when(resource).getInputStream();

        moderationDictionary.loadDictionary();
    }

    @Test
    void containsBadWord() {
        assertTrue(moderationDictionary.containsBadWord("This is a badword1 test"));
        assertFalse(moderationDictionary.containsBadWord("This is a clean comment"));
    }
}
