package faang.school.postservice.config.moderation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class ModerationDictionary {

    public Set<String> curseWords;
    @Value("${post.moderator.path-curse-words}")
    private Path curseWordsPath;

    public void WordSetBean() {
        try (Stream<String> lines = Files.lines(curseWordsPath)) {
            curseWords = lines.map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.warn("Curse dictionary didn't create", e);
        }
    }

    public boolean checkCurseWordsInPost(String text) {
        String convertedText = text.toLowerCase();
        return curseWords.stream().anyMatch(convertedText::contains);
    }
}
