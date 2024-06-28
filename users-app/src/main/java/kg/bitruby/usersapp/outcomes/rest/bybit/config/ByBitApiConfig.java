package kg.bitruby.usersapp.outcomes.rest.bybit.config;

import kg.bitruby.usersapp.client.bybit.api.AccountApi;
import kg.bitruby.usersapp.client.bybit.api.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ByBitApiConfig {

  @Value("${bitruby.bybit.url}")
  private String baseUrl;

  @Bean
  public ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return apiClient;
  }

  @Bean
  public AccountApi accountApi() {
    return new AccountApi(apiClient());
  }
}
