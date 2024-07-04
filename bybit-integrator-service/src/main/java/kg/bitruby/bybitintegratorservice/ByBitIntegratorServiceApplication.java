package kg.bitruby.bybitintegratorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ByBitIntegratorServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ByBitIntegratorServiceApplication.class, args);
  }

}
