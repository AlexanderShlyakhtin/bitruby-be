package kg.bitruby.usersapp.core.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class HmacVerifier {

  @Value("${bitruby.verification.secret-key}")
  String secretKey;

  public boolean isSignatureValid(String xHmacSignature, String payload) {
    // Calculate HMAC-SHA256 hash
    String calculatedSignature = calculateHmacSHA256(payload);
    // Compare calculated signature with the received xHmacSignature
    return calculatedSignature.equalsIgnoreCase(xHmacSignature);
  }

  private String calculateHmacSHA256(String content ) {
    try {
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256Hmac.init(secretKeySpec);
      byte[] hashBytes = sha256Hmac.doFinal(content.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte hashByte : hashBytes) {
        String hex = Integer.toHexString(0xff & hashByte);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
    }
  }
}

