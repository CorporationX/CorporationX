package faang.school.postservice.service.moderation;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ModerationDictionary {

    @Value("${moderation.dictionary.filepath}")
    private String filepath;

    private final Set<String> badWords = new HashSet<>();

    private final ResourceLoader resourceLoader;

    public ModerationDictionary(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadDictionary() throws IOException {
        Resource resource = resourceLoader.getResource(filepath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                badWords.add(line.trim());
            }
        }
    }

    public boolean containsBadWord(String content) {
        for (String word : badWords) {
            if (content.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

