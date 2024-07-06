package kg.bitruby.usersservice.outcomes.postgres.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "kg.bitruby.usersservice.outcomes.postgres.repository" // Adjust this package to your JPA repositories
)
public class JpaConfig {
}
