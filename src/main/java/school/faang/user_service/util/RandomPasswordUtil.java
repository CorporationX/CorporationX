package school.faang.user_service.util;

import java.security.SecureRandom;

public class RandomPasswordUtil {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 12;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }
}