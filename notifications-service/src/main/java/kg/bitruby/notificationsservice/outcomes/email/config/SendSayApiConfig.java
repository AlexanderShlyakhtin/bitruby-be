package kg.bitruby.notificationsservice.outcomes.email.config;


import kg.bitruby.notificationsservice.client.sendsay.api.EmailApi;
import kg.bitruby.notificationsservice.client.sendsay.api.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendSayApiConfig {

  @Value("${bitruby.sendSay.url}")
  private String baseUrl;

  @Bean
  public ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return apiClient;
  }

  @Bean
  public EmailApi emailApi() {
    return new EmailApi(apiClient());
  }
}
