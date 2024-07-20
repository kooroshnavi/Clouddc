package ir.tic.clouddc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class DcimApplication {

	public static void main(String[] args) {
		SpringApplication.run(DcimApplication.class, args);
	}
}