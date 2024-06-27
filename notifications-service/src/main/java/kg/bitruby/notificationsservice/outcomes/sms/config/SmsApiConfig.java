package kg.bitruby.notificationsservice.outcomes.sms.config;


import kg.bitruby.notificationsservice.client.smstraffic.api.SmsApi;
import kg.bitruby.notificationsservice.client.smstraffic.api.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsApiConfig {

  @Value("${bitruby.smsTraffic.url}")
  private String baseUrl;

  @Bean
  public ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return apiClient;
  }

  @Bean
  public SmsApi emailApi() {
    return new SmsApi(apiClient());
  }
}
