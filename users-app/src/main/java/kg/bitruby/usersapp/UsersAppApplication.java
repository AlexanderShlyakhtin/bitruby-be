package kg.bitruby.usersapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class UsersAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(UsersAppApplication.class, args);
  }

}
