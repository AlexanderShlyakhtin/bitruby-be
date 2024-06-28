package kg.bitruby.notificationsservice.outcomes.rest.sendsay.config;


import kg.bitruby.notificationsservice.client.sendsay.api.EmailApi;
import kg.bitruby.notificationsservice.client.sendsay.api.invoker.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SendSayApiConfig {

  @Value("${bitruby.sendSay.url}")
  private String baseUrl;

  @Bean
  public ApiClient emailApiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return apiClient;
  }

  @Bean
  public EmailApi emailApi() {
    return new EmailApi(emailApiClient());
  }
}
