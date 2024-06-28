package kg.bitruby.usersapp.outcomes.rest.veriffClient.config;

import kg.bitruby.usersapp.client.veriff.api.VerificationApi;
import kg.bitruby.usersapp.client.veriff.api.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VeriffApiConfig {

  @Value("${bitruby.verification.url}")
  private String baseUrl;

  @Bean
  public ApiClient veriffAPiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return apiClient;
  }

  @Bean
  public VerificationApi verificationApi() {
    return new VerificationApi(veriffAPiClient());
  }
}
