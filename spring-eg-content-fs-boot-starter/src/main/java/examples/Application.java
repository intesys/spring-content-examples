package examples;

import internal.org.springframework.content.mongo.boot.autoconfigure.MongoContentAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

/**
 * exclude={MongoContentAutoConfiguration.class} is only required because we re-use
 * tests from spring-eg-content-commons that puts spring-content-mongo on the classpath.
 * With Spring Boot 4 the MongoDB repositories auto-configuration lives in the separate
 * spring-boot-data-mongodb module, which is not on the classpath here, so it no longer
 * needs to be excluded explicitly.
 *
 * @author paulcwarren
 *
 */

@SpringBootApplication(exclude={MongoContentAutoConfiguration.class})
@ComponentScan(excludeFilters={
		@Filter(type = FilterType.REGEX,
				pattern = {
						".*MongoConfiguration", 
		})
})
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
