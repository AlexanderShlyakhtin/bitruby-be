package kg.bitruby.bybitintegratorservice.outcomes.rest.bybit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class ByBitSignatureService {

  @Value("${bitruby.bybit.bybit-api-key}")
  private String apiKey;

  @Value("${bitruby.bybit.recvWindow}")
  private Long recvWindow;

  @Value("${bitruby.bybit.bybit-api-secret}")
  private String apiSecret;

  private final ObjectMapper objectMapper;
  public String signPost(Object createSubApiRequest, long epochMilli) {
    String requestBody = writeAsString(createSubApiRequest);
    try {
      String message = epochMilli + apiKey + recvWindow.toString() + requestBody;
      Mac sha256HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256HMAC.init(secretKeySpec);
      byte[] hash = sha256HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
      return Hex.encodeHexString(hash);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new BitrubyRuntimeExpection("Error. While signing the request to ByBit", e);
    }
  }

  private String writeAsString(Object createSubApiRequest) {
    try  {
      return objectMapper.writeValueAsString(createSubApiRequest);
    } catch (Exception exception) {
      throw new BitrubyRuntimeExpection("ObjectMapper can't write requestBody to string");
    }
  }

}
