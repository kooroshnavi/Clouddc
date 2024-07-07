package ir.tic.clouddc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableEncryptableProperties
public class DcimApplication {

	public static void main(String[] args) {
		SpringApplication.run(DcimApplication.class, args);
	}
}