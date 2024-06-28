package faang.school.postservice.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class HashtagSpliterator {

    public static Set<String> getHashtags(String content) {

        String[] words = content.split(" ");

        return Arrays.stream(words)
                .filter(word -> word.startsWith("#") && word.length() > 1)
                .map(word -> word.substring(1))
                .collect(Collectors.toSet());
    }
}
