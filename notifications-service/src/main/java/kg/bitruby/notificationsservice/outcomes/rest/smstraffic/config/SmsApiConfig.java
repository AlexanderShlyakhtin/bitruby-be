package kg.bitruby.notificationsservice.outcomes.rest.smstraffic.config;


import kg.bitruby.notificationsservice.client.smstraffic.api.SmsApi;
import kg.bitruby.notificationsservice.client.smstraffic.api.invoker.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class SmsApiConfig {

  @Value("${bitruby.smsTraffic.url}")
  private String baseUrl;

  private final RestTemplate restTemplate;

  @Bean
  public ApiClient smsTrafficApiClient() {
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(baseUrl);
    return apiClient;
  }

  @Bean
  public SmsApi smsApi() {
    return new SmsApi(smsTrafficApiClient());
  }
}
