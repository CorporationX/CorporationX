package faang.school.postservice.config.moderation;

import jakarta.annotation.Resources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {

    public Set<String> curseWords;
    @Value("${post.path-curse-words}")
    private Resource curseWordsPath;

    public void WordSetBean() {
        try {
            curseWords = new HashSet<>();
            BufferedReader reader = curseWordsPath.getContentAsString(new C);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",");
                for (String element : elements) {
                    words.add(element);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
