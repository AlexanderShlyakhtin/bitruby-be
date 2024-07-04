package kg.bitruby.bybitintegratorservice.common.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class StrongPasswordGenerator {
  private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
  private static final String DIGITS = "0123456789";
  private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?{}[]";
  private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;

  private static final SecureRandom RANDOM = new SecureRandom();

  public String generateStrongPassword(int length) {
    StringBuilder password = new StringBuilder(length);

    // Ensure the password contains at least one character from each character set
    password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
    password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
    password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
    password.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

    // Fill the remaining characters with random characters from all character sets
    for (int i = 4; i < length; i++) {
      password.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
    }

    // Shuffle the password to prevent predictable sequences
    return shuffleString(password.toString());
  }

  private static String shuffleString(String input) {
    char[] a = input.toCharArray();

    for (int i = a.length - 1; i > 0; i--) {
      int j = RANDOM.nextInt(i + 1);
      char tmp = a[i];
      a[i] = a[j];
      a[j] = tmp;
    }

    return new String(a);
  }
}

