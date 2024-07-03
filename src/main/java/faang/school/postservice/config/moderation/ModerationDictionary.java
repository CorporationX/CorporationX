package faang.school.postservice.config.moderation;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class ModerationDictionary {

    @Getter
    private Set<String> curseWords;

    @Value("${post.moderator.path-curse-words}")
    private Path curseWordsPath;

    @PostConstruct
    public void init() {
        try (Stream<String> lines = Files.lines(curseWordsPath)) {
            curseWords = lines.map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.warn("Curse dictionary didn't create", e);
        }
    }

    public boolean checkCurseWordsInPost(String text) {
        String[] words = text.split("\\W+");
        return Arrays.stream(words).anyMatch(curseWords::contains);
    }
}
