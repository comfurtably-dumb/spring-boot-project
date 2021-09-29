package dumb.comfurtably.springbootproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
@SpringBootApplication
public class SpringBootProjectApplication {

	public static void main (String[] args) {
		SpringApplication.run(SpringBootProjectApplication.class, args);
	}

}
