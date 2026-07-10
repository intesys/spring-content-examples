package examples.solrboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * With Spring Boot 4 the MongoDB repositories auto-configuration lives in the separate
 * spring-boot-data-mongodb module, which is not on the classpath here, so no explicit
 * exclusion is required even though commons puts spring-data-mongodb on the classpath.
 *
 * @author paulcwarren
 *
 */

@SpringBootApplication
@EnableConfigurationProperties
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
