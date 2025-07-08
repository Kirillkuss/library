package com.itrail.library.sequrity.generate;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyzа-яё";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZА-ЯЁ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@#$^&+=!№:?:%*(;_)}{";
    private static final int PASSWORD_LENGTH = 24;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Генерация нового пароля
     * @return String
     */
    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder( PASSWORD_LENGTH );
                      password.append( getRandomChar( LOWERCASE ));
                      password.append( getRandomChar( UPPERCASE ));
                      password.append( getRandomChar( DIGITS ));
                      password.append( getRandomChar( SPECIAL_CHARACTERS ));
        String allCharacters = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(getRandomChar(allCharacters));
        }
        return shuffleString(password.toString());
    }

    private char getRandomChar(String characterSet) {
        return characterSet.charAt(random.nextInt(characterSet.length()));
    }

    private String shuffleString(String input) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int randomIndex = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[randomIndex];
            array[randomIndex] = temp;
        }
        return new String(array);
    }
}